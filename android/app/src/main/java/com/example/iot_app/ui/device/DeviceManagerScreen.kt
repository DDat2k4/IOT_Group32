package com.example.iot_app.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.iot_app.data.remote.dto.DeviceDto
import com.example.iot_app.data.remote.dto.SensorDto

@Composable
fun DeviceManagerScreen(viewModel: DeviceViewModel) {
    val devices by viewModel.devices.collectAsState()
    val sensors by viewModel.sensors.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val message by viewModel.message.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var deviceToEdit by remember { mutableStateOf<DeviceDto?>(null) }

    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedDeviceName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadDevices() }

    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text("Thông báo") },
            text = { Text(message!!) },
            confirmButton = { Button(onClick = { viewModel.clearMessage() }) { Text("OK") } }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm thiết bị")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Quản lý thiết bị", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.LightGray).padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mã TB", modifier = Modifier.weight(1.3f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Phòng", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Nhà", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Trạng thái", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("HĐ", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }

            if (loading && devices.isEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(devices.size) { index ->
                    val device = devices[index]
                    DeviceRowItem(
                        device = device,
                        onView = {
                            selectedDeviceName = device.name
                            viewModel.loadSensorsForDevice(device.id)
                            showDetailDialog = true
                        },
                        onEdit = { deviceToEdit = device },
                        onDelete = { viewModel.deleteDevice(device.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    // dialog
    if (showAddDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { code, name, location ->
                viewModel.addDevice(code, name, location)
                showAddDialog = false
            }
        )
    }

    if (deviceToEdit != null) {
        EditDeviceDialog(
            device = deviceToEdit!!,
            onDismiss = { deviceToEdit = null },
            onConfirm = { id, code, name, location, status ->
                viewModel.updateDevice(id, code, name, location, status)
                deviceToEdit = null
            }
        )
    }

    if (showDetailDialog) {
        SensorDetailDialog(
            deviceName = selectedDeviceName,
            sensors = sensors,
            onDismiss = { showDetailDialog = false },
            onUpdateThreshold = { sensor, newVal -> viewModel.updateThreshold(sensor, newVal) }
        )
    }
}

@Composable
fun DeviceRowItem(device: DeviceDto, onView: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(device.deviceCode, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
        Text(device.name, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
        Text(device.location, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
        Text(
            text = device.status,
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Center,
            color = if (device.status == "ACTIVE") Color(0xFF4CAF50) else Color.Red,
            fontWeight = FontWeight.Bold
        )
        Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
            IconButton(onClick = { expanded = true }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Xem") }, onClick = { expanded = false; onView() })
                DropdownMenuItem(text = { Text("Sửa") }, onClick = { expanded = false; onEdit() })
                HorizontalDivider()
                DropdownMenuItem(text = { Text("Xóa", color = Color.Red) }, onClick = { expanded = false; onDelete() })
            }
        }
    }
}

@Composable
fun AddDeviceDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var isLocationError by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        isCodeError = code.isBlank(); isNameError = name.isBlank(); isLocationError = location.isBlank()
        return !isCodeError && !isNameError && !isLocationError
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm thiết bị mới") },
        text = {
            // Arrangement.spacedBy để cách đều
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = code, onValueChange = { code = it; isCodeError = false }, label = { Text("Mã thiết bị *") },
                    isError = isCodeError, supportingText = { if(isCodeError) Text("Bắt buộc nhập") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name, onValueChange = { name = it; isNameError = false }, label = { Text("Tên phòng *") },
                    isError = isNameError, supportingText = { if(isNameError) Text("Bắt buộc nhập") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location, onValueChange = { location = it; isLocationError = false }, label = { Text("Vị trí/Nhà *") },
                    isError = isLocationError, supportingText = { if(isLocationError) Text("Bắt buộc nhập") }, modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = { if (validate()) onConfirm(code, name, location) }) { Text("Thêm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

// dialog ở cập nhật tb
@Composable
fun EditDeviceDialog(
    device: DeviceDto,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String, String, String) -> Unit
) {
    var code by remember { mutableStateOf(device.deviceCode) }
    var name by remember { mutableStateOf(device.name) }
    var location by remember { mutableStateOf(device.location) }
    var status by remember { mutableStateOf(device.status) }
    var expandedStatus by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var isLocationError by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        isNameError = name.isBlank(); isLocationError = location.isBlank()
        return !isNameError && !isLocationError
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cập nhật thiết bị") },
        text = {
            //các ô cách đều nhau
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = code, onValueChange = {}, label = { Text("Mã thiết bị") },
                    modifier = Modifier.fillMaxWidth(), enabled = false, supportingText = { Text("")}, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.Gray)
                )

                OutlinedTextField(
                    value = name, onValueChange = { name = it; isNameError = false }, label = { Text("Tên phòng *") },
                    isError = isNameError, supportingText = { if(isNameError) Text("Không được để trống") else Text("") }, modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location, onValueChange = { location = it; isLocationError = false }, label = { Text("Vị trí/Nhà *") },
                    isError = isLocationError, supportingText = { if(isLocationError) Text("Không được để trống") else Text("") }, modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = status, onValueChange = {}, label = { Text("Trạng thái") }, readOnly = true,
                        modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }, supportingText = { Text("") }
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { expandedStatus = true })
                    DropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }, modifier = Modifier.fillMaxWidth(0.7f)) {
                        DropdownMenuItem(text = { Text("ACTIVE", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold) }, onClick = { status = "ACTIVE"; expandedStatus = false })
                        DropdownMenuItem(text = { Text("INACTIVE", color = Color.Red, fontWeight = FontWeight.Bold) }, onClick = { status = "INACTIVE"; expandedStatus = false })
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if(validate()) onConfirm(device.id, code, name, location, status) }) { Text("Lưu") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@Composable
fun SensorDetailDialog(
    deviceName: String,
    sensors: List<SensorDto>,
    onDismiss: () -> Unit,
    onUpdateThreshold: (SensorDto, Double) -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Cảm biến: $deviceName", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFEEEEEE)).border(1.dp, Color.LightGray).padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Tên", Modifier.weight(1.2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Trạng thái", Modifier.weight(1.2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Ngưỡng", Modifier.weight(1.2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("HĐ", Modifier.weight(0.5f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }

                if (sensors.isEmpty()) Text("Không có dữ liệu", Modifier.padding(16.dp).align(Alignment.CenterHorizontally))

                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(sensors.size) { index -> SensorRow(sensors[index], onUpdateThreshold) }
                }

                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Đóng") }
            }
        }
    }
}

@Composable
fun SensorRow(sensor: SensorDto, onUpdate: (SensorDto, Double) -> Unit) {
    var showEditDialog by remember { mutableStateOf(false) }
    // caajp nhật status từ b.e
    val statusColor = if (sensor.status == "ACTIVE") Color(0xFF4CAF50) else Color.Red

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(sensor.name, Modifier.weight(1.2f), textAlign = TextAlign.Center)
        Text(text = sensor.status, Modifier.weight(1.2f), color = statusColor, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
        Text("${sensor.maxValue} ${sensor.unit}", Modifier.weight(1.2f), textAlign = TextAlign.Center)
        Text(text = "Sửa", modifier = Modifier.weight(0.5f).clickable { showEditDialog = true }, textAlign = TextAlign.Center, color = Color.Blue, fontWeight = FontWeight.Bold)
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

    if (showEditDialog) {
        var newValStr by remember { mutableStateOf(sensor.maxValue.toString()) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Cài đặt ngưỡng ${sensor.name}") },
            text = { OutlinedTextField(value = newValStr, onValueChange = { newValStr = it }, label = { Text("Giá trị mới (${sensor.unit})") }, singleLine = true) },
            confirmButton = {
                Button(onClick = {
                    val doubleVal = newValStr.toDoubleOrNull()
                    if (doubleVal != null) { onUpdate(sensor, doubleVal); showEditDialog = false }
                }) { Text("Lưu") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Hủy") } }
        )
    }
}