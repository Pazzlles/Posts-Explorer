package com.example.task3.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Immutable
data class CountryDetailUiState(
    val countryCode: String,
    val title: String = "Детали поста",
    val content: CountryDetailContentState = CountryDetailContentState.Loading,
)

@Immutable
sealed interface CountryDetailContentState {
    data object Loading : CountryDetailContentState

    data class Error(
        val message: String,
    ) : CountryDetailContentState

    data class Success(
        val country: CountryDetailBodyUiModel,
    ) : CountryDetailContentState
}

@Immutable
data class CountryDetailBodyUiModel(
    val title: String,
    val body: String,
    val properties: List<CountryPropertyUiModel>,
)

@Immutable
data class CountryPropertyUiModel(
    val label: String,
    val value: String,
)

sealed interface CountryDetailEvent {
    data object BackClicked : CountryDetailEvent
    data object RetryClicked : CountryDetailEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    state: CountryDetailUiState,
    onEvent: (CountryDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(state.title) },
                navigationIcon = {
                    TextButton(onClick = { onEvent(CountryDetailEvent.BackClicked) }) {
                        Text("Назад")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val content = state.content) {
            CountryDetailContentState.Loading -> {
                DetailMessageState(
                    title = "Загрузка деталей...",
                    message = "Получаем информацию о выбранном посте.",
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is CountryDetailContentState.Error -> {
                DetailErrorState(
                    message = content.message,
                    onRetry = { onEvent(CountryDetailEvent.RetryClicked) },
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is CountryDetailContentState.Success -> {
                CountryDetailContent(
                    country = content.country,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun CountryDetailContent(
    country: CountryDetailBodyUiModel,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = country.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = country.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        items(
            items = country.properties,
            key = { property -> property.label },
        ) { property ->
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = property.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = property.value,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailMessageState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DetailErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Ошибка загрузки",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
