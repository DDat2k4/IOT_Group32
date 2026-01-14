package com.example.iot_app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.iot_app.data.remote.dto.RegisterRequest

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    navController: NavHostController,
    onRegisterSuccess: () -> Unit
) {
    val loading by viewModel.loading.observeAsState(false)
    val message by viewModel.message.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Logic Validation
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordMatch = password == confirmPassword && password.isNotEmpty()
    val isFormValid = fullName.isNotBlank() &&
            username.length >= 3 &&
            isEmailValid &&
            password.length >= 6 &&
            isPasswordMatch

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            if (it == "Đăng ký thành công") onRegisterSuccess()
            viewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Tạo tài khoản", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(32.dp))

            // Họ và tên
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                // Thêm supportingText trống để ô này có chiều cao bằng các ô có báo lỗi
                supportingText = { Text("") }
            )
            Spacer(Modifier.height(8.dp))

            // Tên đăng nhập
            OutlinedTextField(
                value = username, onValueChange = { username = it },
                label = { Text("Tên đăng nhập") }, modifier = Modifier.fillMaxWidth(),
                isError = username.isNotEmpty() && username.length < 3,
                supportingText = {
                    if(username.isNotEmpty() && username.length < 3) Text("Tối thiểu 3 ký tự") else Text("")
                },
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            // Email
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                isError = email.isNotEmpty() && !isEmailValid,
                supportingText = {
                    if(email.isNotEmpty() && !isEmailValid) Text("Email không hợp lệ") else Text("")
                },
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            // Mật khẩu
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Mật khẩu") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                    }
                },
                isError = password.isNotEmpty() && password.length < 6,
                supportingText = {
                    if(password.isNotEmpty() && password.length < 6) Text("Mật khẩu phải từ 6 ký tự") else Text("")
                },
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            //Xác nhận mật khẩu
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận mật khẩu") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                    }
                },
                isError = confirmPassword.isNotEmpty() && !isPasswordMatch,
                supportingText = {
                    if(confirmPassword.isNotEmpty() && !isPasswordMatch) Text("Mật khẩu không khớp") else Text("")
                },
                shape = RoundedCornerShape(12.dp), singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.register(RegisterRequest(username, password, fullName, email)) { onRegisterSuccess() } },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && isFormValid,
                shape = RoundedCornerShape(12.dp)
            ) { Text("Đăng ký") }

            TextButton(onClick = { navController.popBackStack() }) { Text("Đã có tài khoản? Đăng nhập") }
        }

        if (loading) {
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
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}