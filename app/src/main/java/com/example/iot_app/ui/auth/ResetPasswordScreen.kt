package com.example.iot_app.ui.auth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.input.PasswordVisualTransformation


@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel,
    onDone: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    val message by viewModel.message.observeAsState()

    LaunchedEffect(message) {
        if (message == "Đổi mật khẩu thành công") {
            onDone()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Đặt lại mật khẩu", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(email, { email = it }, label = { Text("Email") })
        OutlinedTextField(otp, { otp = it }, label = { Text("OTP") })
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Mật khẩu mới") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.resetPassword(email, otp, newPassword)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xác nhận")
        }

        message?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}
