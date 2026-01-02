package com.example.iot_app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iot_app.ui.user.UserViewModel // Đảm bảo import đúng đường dẫn ViewModel của bạn
import java.util.Calendar

@Composable
fun HomeScreen(viewModel: UserViewModel) {
    // Lắng nghe dữ liệu profile từ ViewModel
    val profile by viewModel.profile.observeAsState()

    // Nếu chưa có dữ liệu thì gọi hàm load
    LaunchedEffect(Unit) {
        if (profile == null) {
            viewModel.loadProfile()
        }
    }

    // Xử lý logic thời gian thực
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 5..11 -> "Chào buổi sáng"      // 05:00  11:59
        in 12..17 -> "Chào buổi chiều"    // 12:00  17:59
        in 18..21 -> "Chào buổi tối"      // 18:00  21:59
        else -> "Chúc ngủ ngon"           // 22:00  04:59
    }

    // lấy tên hiển thị username, nếu null thì hiện "User"
    val displayName = profile?.username ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa nội dung theo chiều ngang
    ) {
        Text(
            text = "$greeting, $displayName!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center // Căn giữa văn bản nếu bị xuống dòng
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Hãy cùng chúng tôi giữ gìn an toàn cho ngôi nhà và gia đình bạn",
            textAlign = TextAlign.Center // Căn giữa văn bản
        )
    }
}