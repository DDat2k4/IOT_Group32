package com.example.iot_app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

//visco
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navController: NavController
) {
    val rooms by viewModel.rooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startAutoRefresh()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tổng quan khu vực", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rooms) { room ->
                    RoomItem(room) { navController.navigate("room_detail/${room.deviceCode}") }
                }
            }
        }
    }
}

@Composable
fun RoomItem(room: RoomUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.aspectRatio(1f).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = room.statusColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = room.roomName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = room.deviceCode, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

// chi tiết từng phòng
@Composable
fun RoomDetailScreen(
    deviceCode: String,
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val room = remember(deviceCode) { viewModel.getRoomDetail(deviceCode) }
    val selectedType by viewModel.selectedChartType.collectAsState()
    val chartEntries by viewModel.chartEntries.collectAsState()
    val isChartLoading by viewModel.isChartLoading.collectAsState()
    val currentThreshold by viewModel.currentThreshold.collectAsState()
    val chartMaxY by viewModel.chartMaxY.collectAsState()

    LaunchedEffect(deviceCode) { viewModel.initChart(deviceCode) }

    if (room == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Không tìm thấy dữ liệu thiết bị $deviceCode") }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(room.roomName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Mã TB: ${room.deviceCode}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Biểu đồ thống kê (${getSensorName(selectedType)}):", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // biểu đồ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isChartLoading) {
                CircularProgressIndicator()
            } else if (chartEntries.isEmpty()) {
                Text("Chưa có dữ liệu lịch sử", color = Color.Gray)
            } else {
                val chartModel = entryModelOf(chartEntries)

                // cấu hình màu biểu đồ
                val myLineSpec = lineSpec(
                    lineColor = Color(0xFF1976D2),
                    lineThickness = 3.dp
                )

                // Vẽ biểu đồ trong Box để overlay đường ngưỡng
                Box(modifier = Modifier.fillMaxSize()) {
                    Chart(
                        chart = lineChart(
                            lines = listOf(myLineSpec),
                            axisValuesOverrider = AxisValuesOverrider.fixed(
                                minY = 0f,
                                maxY = chartMaxY,
                                minX = 0f,
                                maxX = 29f
                            )
                        ),
                        model = chartModel,
                        startAxis = rememberStartAxis(valueFormatter = { value, _ -> value.toInt().toString() }),
                        bottomAxis = rememberBottomAxis(guideline = null),
                        modifier = Modifier.fillMaxSize()
                    )

                    // Vẽ đường ngưỡng màu đỏ bằng Canvas và tính toán chính xác với padding
                    if (currentThreshold > 0 && chartMaxY > 0) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Ước lượng padding của Chart (trục Y chiếm ~40dp, trục X chiếm ~30dp)
                            val leftPadding = 40.dp.toPx()
                            val bottomPadding = 30.dp.toPx()
                            val topPadding = 10.dp.toPx()
                            val rightPadding = 10.dp.toPx()

                            // Vùng vẽ thực tế của biểu đồ
                            val chartDrawHeight = size.height - topPadding - bottomPadding

                            // Tính tỷ lệ vị trí của threshold
                            val ratio = currentThreshold.toFloat() / chartMaxY

                            // Vị trí Y
                            val yPosition = topPadding + (chartDrawHeight * (1f - ratio))

                            drawLine(
                                color = Color.Red,
                                start = Offset(leftPadding, yPosition),
                                end = Offset(size.width - rightPadding, yPosition),
                                strokeWidth = 3f
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SensorSelectButton("TEMP", "Nhiệt độ", selectedType) { viewModel.onChartSensorSelected(deviceCode, "TEMP") }
            SensorSelectButton("FLAME", "Lửa", selectedType) { viewModel.onChartSensorSelected(deviceCode, "FLAME") }
            SensorSelectButton("CO", "Khí CO", selectedType) { viewModel.onChartSensorSelected(deviceCode, "CO") }
            SensorSelectButton("MQ2", "Khí Gas", selectedType) { viewModel.onChartSensorSelected(deviceCode, "MQ2") }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Thông số hiện tại", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp)).padding(8.dp)) {
            room.sensors.forEach { sensor -> SensorRow(sensor) }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

//tp phụ
@Composable
fun SensorSelectButton(type: String, label: String, selectedType: String, onClick: () -> Unit) {
    val isSelected = type == selectedType
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f)
    val contentColor = if (isSelected) Color.White else Color.Black
    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors(containerColor = containerColor, contentColor = contentColor),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = Modifier.width(85.dp)
    ) { Text(text = label, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
}

fun getSensorName(type: String): String {
    return when(type) {
        "TEMP" -> "Nhiệt độ"
        "FLAME" -> "Lửa"
        "CO" -> "Khí CO"
        "MQ2" -> "Khí Gas"
        else -> type
    }
}

@Composable
fun SensorRow(sensor: SensorUiData) {
    val valueRatio =
        if (sensor.threshold > 0) sensor.value / sensor.threshold else 0.0

    val valueColor = when {
        sensor.threshold <= 0 -> Color(0xFF2E7D32)
        valueRatio >= 1.0 -> Color(0xFFD32F2F)
        valueRatio >= 0.8 -> Color(0xFFF9A825)
        else -> Color(0xFF2E7D32)
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "${sensor.name} (${sensor.type})", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "${sensor.value} ${sensor.unit}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
            if (sensor.threshold > 0) {
                Text(text = "Ngưỡng: ${sensor.threshold} ${sensor.unit}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
}