package com.example.iot_app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel,
    email: String,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val message by viewModel.message.observeAsState()
    val loading by viewModel.loading.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }

    // -validation
    val isOtpError = otp.isNotEmpty() && otp.length < 4 //OTP cần ít nhất 4 ký tự
    val isPasswordError = newPassword.isNotEmpty() && newPassword.length < 6
    val isFormValid = otp.isNotEmpty() && newPassword.length >= 6 && !isOtpError && !isPasswordError

    LaunchedEffect(message) {
        message?.let {
            if (it == "Đổi mật khẩu thành công") {
                viewModel.clearMessage()
                onDone()
            } else {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessage()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Đặt lại mật khẩu",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))
            Text("Mã OTP đã được gửi về email: $email", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(32.dp))

            // Ô nhập OTP
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Mã OTP") },
                modifier = Modifier.fillMaxWidth(),
                isError = isOtpError,
                supportingText = {
                    if (isOtpError) Text("Mã OTP không hợp lệ")
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Tăng khoảng cách để có chỗ cho supportingText
            Spacer(Modifier.height(20.dp))

            // Ô nhập Mật khẩu mới
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Mật khẩu mới") },
                modifier = Modifier.fillMaxWidth(),
                isError = isPasswordError,
                supportingText = {
                    if (isPasswordError) Text("Mật khẩu mới phải từ 6 ký tự trở lên")
                },
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true
            )

            // Spacer dành cho supportingText của ô mật khẩu
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.resetPassword(email, otp, newPassword) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && isFormValid,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xác nhận thay đổi")
            }

            TextButton(
                onClick = {
                    viewModel.clearMessage()
                    onCancel()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Hủy và quay lại")
            }
        }

        // Lớp phủ Loading
        if (loading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("Đang xử lý...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}