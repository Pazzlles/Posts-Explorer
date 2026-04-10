package com.example.task3.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CountryDetailRoute(
    countryCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountryDetailViewModel = viewModel(
        factory = CountryDetailViewModel.factory(countryCode),
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CountryDetailScreen(
        state = state,
        modifier = modifier,
        onEvent = { event ->
            when (event) {
                CountryDetailEvent.BackClicked -> onBack()
                CountryDetailEvent.RetryClicked -> viewModel.onEvent(event)
            }
        },
    )
}
