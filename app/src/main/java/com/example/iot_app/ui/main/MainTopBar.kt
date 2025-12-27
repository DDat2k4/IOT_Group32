package com.example.iot_app.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    onMenuClick: () -> Unit,
    onProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar( // Sử dụng CenterAligned để chữ ở giữa
        title = {
            Text(
                text = "Màn hình chính",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                }
                // Menu thả xuống giống như hình bạn mong muốn
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Thông tin cá nhân") },
                        onClick = { showMenu = false; onProfile() }
                    )
                    DropdownMenuItem(
                        text = { Text("Đổi mật khẩu") },
                        onClick = { showMenu = false; onChangePassword() }
                    )
                    HorizontalDivider() // Vạch kẻ ngang cho đẹp
                    DropdownMenuItem(
                        text = { Text("Đăng xuất") },
                        onClick = { showMenu = false; onLogout() }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}