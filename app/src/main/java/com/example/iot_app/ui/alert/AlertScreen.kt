package com.example.iot_app.ui.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.iot_app.data.remote.dto.AlertDto

@Composable
fun AlertScreen(viewModel: AlertViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshData() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Lịch sử Cảnh báo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (loading && alerts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(alerts) { alert ->
                    AlertDetailedItem(alert)
                }
            }
        }
    }
}

@Composable
fun AlertDetailedItem(alert: AlertDto) {
    val (statusColor, statusBg, statusText) = when (alert.level) {
        "HIGH" -> Triple(Color(0xFFD32F2F), Color(0xFFFFEBEE), "KHẨN CẤP")
        "MEDIUM" -> Triple(Color(0xFFF9A825), Color(0xFFFFF3E0), "NGUY HIỂM")
        else -> Triple(Color(0xFF388E3C), Color(0xFFE8F5E9), "BÌNH THƯỜNG")
    }

    val roomDisplay = alert.roomName ?: "Cảnh báo mới!"
    val deviceDisplay = alert.deviceCode ?: "Unknown"
    val unit = alert.sensorUnit ?: ""

    val timeDisplay = try {
        alert.createdAt.replace("T", " ").substringBefore(".")
    } catch (e: Exception) { alert.createdAt }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = roomDisplay, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = deviceDisplay, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Surface(
                    color = statusBg, shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                ) {
                    Text(text = statusText, color = statusColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))

            // tên cảm biến
            InfoRow("Tên cảm biến", alert.alertType ?: "N/A")

            // giá trị đo được
            val valueText = if (alert.value != null) "${alert.value} $unit" else "---"
            InfoRow("Giá trị đo", valueText, isHighlight = true, highlightColor = statusColor)

            // ngưỡng an toàn
            val thresholdText = if (alert.threshold != null) "${alert.threshold} $unit" else "---"
            InfoRow("Ngưỡng an toàn", thresholdText)

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(text = "🕒 $timeDisplay", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isHighlight: Boolean = false, highlightColor: Color = Color.Black) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium, color = if (isHighlight) highlightColor else Color.Black)
    }
}