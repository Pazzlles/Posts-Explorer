package com.example.task3.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.task3.data.CountryRepository
import com.example.task3.data.ServiceLocator
import com.example.task3.data.model.CountryListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CountriesViewModel(
    private val repository: CountryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CountriesUiState())
    val uiState: StateFlow<CountriesUiState> = _uiState.asStateFlow()

    private var lastSubmittedQuery: String = ""

    init {
        loadCountries(query = "")
    }

    fun onEvent(event: CountriesEvent) {
        when (event) {
            is CountriesEvent.QueryChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(searchQuery = event.value)
                }
            }

            CountriesEvent.SearchSubmitted -> {
                val query = _uiState.value.searchQuery.trim()
                lastSubmittedQuery = query
                loadCountries(query = query)
            }

            CountriesEvent.ClearSearchClicked -> {
                lastSubmittedQuery = ""
                _uiState.update { currentState ->
                    currentState.copy(searchQuery = "")
                }
                loadCountries(query = "")
            }

            CountriesEvent.RetryClicked -> {
                loadCountries(query = lastSubmittedQuery)
            }

            is CountriesEvent.CountryClicked -> Unit
        }
    }

    private fun loadCountries(query: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(content = CountriesContentState.Loading)
            }

            runCatching {
                repository.getCountries(query)
            }.onSuccess { countries ->
                _uiState.update { currentState ->
                    currentState.copy(content = countries.toContentState(query = query))
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to load countries", throwable)
                _uiState.update { currentState ->
                    currentState.copy(
                        content = CountriesContentState.Error(
                            message = throwable.toUserMessage(),
                        ),
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "CountriesViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CountriesViewModel(repository = ServiceLocator.countryRepository)
            }
        }
    }
}

private fun List<CountryListItem>.toContentState(query: String): CountriesContentState {
    if (isEmpty()) {
        return CountriesContentState.Empty(
            title = "Ничего не найдено",
            message = if (query.isBlank()) {
                "API вернул пустой список стран."
            } else {
                "По запросу \"$query\" ничего не найдено."
            },
        )
    }

    val summary = if (query.isBlank()) {
        "Найдено постов: $size"
    } else {
        "Найдено постов для userId=$query: $size"
    }

    return CountriesContentState.Success(
        summary = summary,
        items = map { country ->
            CountryCardUiModel(
                id = country.id.toString(),
                title = country.title,
                subtitle = "Post #${country.id} · User #${country.userId}",
                bodyPreview = country.preview,
            )
        },
    )
}

private fun Throwable.toUserMessage(): String {
    return when (this) {
        is IOException -> "Проверьте подключение к интернету и попробуйте снова."
        is HttpException -> "Сервер вернул ошибку ${code()}."
        else -> message ?: "Не удалось загрузить список постов."
    }
}
