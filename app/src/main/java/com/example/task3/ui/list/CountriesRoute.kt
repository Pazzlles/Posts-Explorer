package com.example.task3.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CountriesRoute(
    onOpenCountry: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountriesViewModel = viewModel(factory = CountriesViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CountriesScreen(
        state = state,
        modifier = modifier,
        onEvent = { event ->
            when (event) {
                is CountriesEvent.CountryClicked -> onOpenCountry(event.countryCode)
                else -> viewModel.onEvent(event)
            }
        },
    )
}
