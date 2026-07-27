package com.tonespace.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tonespace.app.ui.screens.profile.ProfileMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var autoPlay by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "General",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsSwitchItem(
                        title = "Dark Mode",
                        subtitle = "Use dark theme",
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    )
                    HorizontalDivider()
                    SettingsSwitchItem(
                        title = "Notifications",
                        subtitle = "Receive push notifications",
                        checked = notifications,
                        onCheckedChange = { notifications = it }
                    )
                    HorizontalDivider()
                    SettingsSwitchItem(
                        title = "Auto-Play",
                        subtitle = "Auto-play sounds in feed",
                        checked = autoPlay,
                        onCheckedChange = { autoPlay = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "About",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        title = "App Version",
                        subtitle = "1.0.0",
                        onClick = { }
                    )
                    HorizontalDivider()
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        title = "Privacy Policy",
                        subtitle = "How we handle your data",
                        onClick = { }
                    )
                    HorizontalDivider()
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        title = "Terms of Service",
                        subtitle = "Usage terms",
                        onClick = { }
                    )
                    HorizontalDivider()
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        title = "Licenses",
                        subtitle = "Open source licenses",
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}