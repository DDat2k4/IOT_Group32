package com.example.iot_app.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(
    showDashboardBadge: Boolean, //chấm đỏ ở dashboard
    onItemClick: (String) -> Unit
) {
    // ModalDrawerSheet tạo nền trắng/xám nhẹ, bo góc phải và đổ bóng
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp) // Giới hạn độ rộng của menu
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Tiêu đề menu
        Text(
            text = "Ứng dụng IoT",
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp)) // Vạch kẻ ngang
        Spacer(modifier = Modifier.height(12.dp))

        // Sử dụng NavigationDrawerItem để có icon và hiệu ứng highlight khi bấm
        NavigationDrawerItem(
            label = { Text("Trang chủ") },
            selected = false,
            onClick = { onItemClick("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text("Dashboard") },
            selected = false,
            onClick = { onItemClick("dashboard") },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),

            badge = {
                if (showDashboardBadge) {
                    // Dấu chấm than đỏ báo hiệu nguy hiểm
                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                        Text("!")
                    }
                }
            }
        )

        NavigationDrawerItem(
            label = { Text("Thiết bị") },
            selected = false,
            onClick = { onItemClick("devices") },
            icon = { Icon(Icons.Default.Devices, contentDescription = null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )

        NavigationDrawerItem(
            label = { Text("Cảnh báo") },
            selected = false,
            onClick = { onItemClick("alerts") },
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}