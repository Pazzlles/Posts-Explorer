package com.example.task3.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Immutable
data class CountriesUiState(
    val title: String = "Posts Explorer",
    val searchQuery: String = "",
    val content: CountriesContentState = CountriesContentState.Loading,
)

@Immutable
sealed interface CountriesContentState {
    data object Loading : CountriesContentState

    data class Error(
        val message: String,
    ) : CountriesContentState

    data class Empty(
        val title: String,
        val message: String,
    ) : CountriesContentState

    data class Success(
        val summary: String,
        val items: List<CountryCardUiModel>,
    ) : CountriesContentState
}

@Immutable
data class CountryCardUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val bodyPreview: String,
)

sealed interface CountriesEvent {
    data class QueryChanged(val value: String) : CountriesEvent
    data object SearchSubmitted : CountriesEvent
    data object ClearSearchClicked : CountriesEvent
    data object RetryClicked : CountriesEvent
    data class CountryClicked(val countryCode: String) : CountriesEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreen(
    state: CountriesUiState,
    onEvent: (CountriesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(state.title) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(CountriesEvent.QueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Фильтр по userId") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onEvent(CountriesEvent.SearchSubmitted)
                    },
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onEvent(CountriesEvent.SearchSubmitted) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Искать")
                }

                TextButton(
                    onClick = { onEvent(CountriesEvent.ClearSearchClicked) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Сбросить")
                }
            }

            when (val content = state.content) {
                CountriesContentState.Loading -> {
                    CountriesMessageState(
                        title = "Загрузка списка...",
                        message = "Получаем данные из JSONPlaceholder API.",
                        modifier = Modifier.weight(1f),
                    )
                }

                is CountriesContentState.Error -> {
                    CountriesErrorState(
                        message = content.message,
                        onRetry = { onEvent(CountriesEvent.RetryClicked) },
                        modifier = Modifier.weight(1f),
                    )
                }

                is CountriesContentState.Empty -> {
                    CountriesMessageState(
                        title = content.title,
                        message = content.message,
                        modifier = Modifier.weight(1f),
                    )
                }

                is CountriesContentState.Success -> {
                    CountriesListContent(
                        summary = content.summary,
                        items = content.items,
                        onCountryClick = { countryCode ->
                            onEvent(CountriesEvent.CountryClicked(countryCode))
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CountriesListContent(
    summary: String,
    items: List<CountryCardUiModel>,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(
                text = summary,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        items(
            items = items,
            key = { item -> item.id },
        ) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCountryClick(item.id) },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = item.bodyPreview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CountriesMessageState(
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
private fun CountriesErrorState(
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
