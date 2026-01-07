package com.example.iot_app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iot_app.ui.user.UserViewModel
import java.util.Calendar

@Composable
fun HomeScreen(viewModel: UserViewModel) {
    val profile by viewModel.profile.collectAsState()

    // xử lý logic tgian thực
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 5..11 -> "Chào buổi sáng"      // 05:00 - 11:59
        in 12..17 -> "Chào buổi chiều"    // 12:00 - 17:59
        in 18..21 -> "Chào buổi tối"      // 18:00 - 21:59
        else -> "Chúc ngủ ngon"           // 22:00 - 04:59
    }

    // Lấy tên hiển thị, ưu tiên fullName, nếu null lấy username
    val displayName = profile?.fullName ?: profile?.username ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$greeting, $displayName!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Hãy cùng chúng tôi giữ gìn an toàn cho ngôi nhà và gia đình bạn",
            textAlign = TextAlign.Center
        )
    }
}