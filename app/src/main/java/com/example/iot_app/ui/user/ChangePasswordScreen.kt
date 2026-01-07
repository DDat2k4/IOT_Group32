package com.example.iot_app.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ChangePasswordScreen(viewModel: UserViewModel) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    // Tách riêng 2 biến mắt cho mật khẩu cũ và mới
    var oldPassVisible by remember { mutableStateOf(false) }
    var newPassVisible by remember { mutableStateOf(false) }

    // --- SỬA LỖI: Dùng collectAsState thay vì observeAsState ---
    val message by viewModel.message.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    // ---------------------------------------------------------

    val snackbarHostState = remember { SnackbarHostState() }

    // Kiểm tra điều kiện nhập liệu
    val isFormValid = oldPass.isNotEmpty() && newPass.length >= 6

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text("Đổi mật khẩu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = oldPass, onValueChange = { oldPass = it },
                    label = { Text("Mật khẩu cũ") }, modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (oldPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { oldPassVisible = !oldPassVisible }) {
                            Icon(imageVector = if (oldPassVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = newPass, onValueChange = { newPass = it },
                    label = { Text("Mật khẩu mới") }, modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { newPassVisible = !newPassVisible }) {
                            Icon(imageVector = if (newPassVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                        }
                    },
                    isError = newPass.isNotEmpty() && newPass.length < 6,
                    supportingText = { if(newPass.isNotEmpty() && newPass.length < 6) Text("Mật khẩu mới phải từ 6 ký tự") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = { viewModel.changePassword(oldPass, newPass) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading && isFormValid,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cập nhật mật khẩu")
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