package com.tonespace.app.ui.screens.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonespace.app.data.model.SoundCategory
import com.tonespace.app.ui.components.CategoryChip
import com.tonespace.app.ui.components.EmptyScreen
import com.tonespace.app.ui.components.ErrorScreen
import com.tonespace.app.ui.components.LoadingScreen
import com.tonespace.app.ui.components.SoundCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onSoundClick: (String) -> Unit,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Browse",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        val categories = SoundCategory.entries
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    label = "All",
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) }
                )
            }
            items(categories) { category ->
                CategoryChip(
                    label = category.name.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    selected = uiState.selectedCategory == category,
                    onClick = { viewModel.selectCategory(category) }
                )
            }
        }

        when {
            uiState.isLoading -> LoadingScreen()
            uiState.error != null -> ErrorScreen(
                message = uiState.error ?: "Unknown error",
                onRetry = { viewModel.refresh() }
            )
            uiState.sounds.isEmpty() -> EmptyScreen("No sounds in this category")
            else -> PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.sounds) { sound ->
                        SoundCard(
                            sound = sound,
                            onClick = { onSoundClick(sound.id) }
                        )
                    }
                }
            }
        }
    }
}