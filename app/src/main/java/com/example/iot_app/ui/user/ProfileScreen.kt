package com.example.iot_app.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(viewModel: UserViewModel) {
    // --- SỬA LỖI: Dùng collectAsState ---
    val profile by viewModel.profile.collectAsState()
    val message by viewModel.message.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    // ------------------------------------

    var isEditing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Dùng key là profile để reset biến tạm mỗi khi đổi Account
    var tempFullName by remember(profile) { mutableStateOf(profile?.fullName ?: "") }
    var tempEmail by remember(profile) { mutableStateOf(profile?.email ?: "") }

    // Cập nhật lại biến tạm khi profile thực sự có dữ liệu từ API
    LaunchedEffect(profile) {
        profile?.let {
            tempFullName = it.fullName
            tempEmail = it.email
        }
    }

    // Đã xóa LaunchedEffect(Unit) { viewModel.loadProfile() }
    // vì ViewModel mới có khối init {} tự load rồi.

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(tempEmail).matches()
    val isFormValid = tempFullName.isNotBlank() && isEmailValid

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            // Nếu chưa có bất kỳ dữ liệu nào thì hiện Loading
            if (profile == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(24.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "@${profile?.username}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(Modifier.height(32.dp))

                    // Họ và tên
                    OutlinedTextField(
                        value = tempFullName,
                        onValueChange = { tempFullName = it },
                        label = { Text("Họ tên") },
                        readOnly = !isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = isEditing && tempFullName.isBlank(),
                        supportingText = {
                            if (isEditing && tempFullName.isBlank()) Text("Họ tên không được để trống") else Text("")
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Email
                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text("Email") },
                        readOnly = !isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = isEditing && !isEmailValid,
                        supportingText = {
                            if (isEditing && !isEmailValid) Text("Email không đúng định dạng") else Text("")
                        }
                    )

                    Spacer(Modifier.height(32.dp))

                    if (!isEditing) {
                        Button(
                            onClick = { isEditing = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Chỉnh sửa thông tin")
                        }
                    } else {
                        Row(Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = {
                                    isEditing = false
                                    tempFullName = profile?.fullName ?: ""
                                    tempEmail = profile?.email ?: ""
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Hủy")
                            }
                            Spacer(Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    viewModel.updateProfile(tempFullName, tempEmail)
                                    isEditing = false
                                },
                                enabled = isFormValid && !isLoading,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Lưu")
                            }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.3f)) {
                Box(contentAlignment = Alignment.Center) {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("Đang xử lý...")
                        }
                    }
                }
            }
        }
    }
}