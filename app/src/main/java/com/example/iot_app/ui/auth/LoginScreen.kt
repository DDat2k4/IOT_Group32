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
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val message by viewModel.message.observeAsState()
    val loading by viewModel.loading.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // validation
    val isUsernameError = username.isNotEmpty() && username.length < 3
    val isPasswordError = password.isNotEmpty() && password.length < 6
    val isFormValid = username.isNotEmpty() && password.isNotEmpty() && !isUsernameError && !isPasswordError

    LaunchedEffect(message) {
        message?.let {
            if (it == "Đăng nhập thành công") {
                onLoginSuccess()
            } else {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.clearMessage()
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
                "Đăng nhập",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            // Ô nhập Tên đăng nhập
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên đăng nhập") },
                modifier = Modifier.fillMaxWidth(),
                isError = isUsernameError,
                supportingText = {
                    if (isUsernameError) Text("Tên đăng nhập phải từ 3 ký tự")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Tăng khoảng cách để có chỗ cho supportingText (20dp)
            Spacer(Modifier.height(20.dp))

            // Ô nhập Mật khẩu
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = isPasswordError,
                supportingText = {
                    if (isPasswordError) Text("Mật khẩu phải từ 6 ký tự")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Spacer dành cho supportingText của ô mật khẩu
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(username, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && isFormValid, // Chỉ bật nút khi form hợp lệ
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Đăng nhập")
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Đăng ký tài khoản")
                }
                TextButton(onClick = onForgotPassword) {
                    Text("Quên mật khẩu?", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        // Lớp phủ Loading đồng bộ
        if (loading) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.3f)) {
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
                            Text("Đang xác thực...", style = MaterialTheme.typography.bodyMedium)
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