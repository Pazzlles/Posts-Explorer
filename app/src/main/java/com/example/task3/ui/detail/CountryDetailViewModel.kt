package com.example.task3.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.task3.data.CountryRepository
import com.example.task3.data.ServiceLocator
import com.example.task3.data.model.CountryDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CountryDetailViewModel(
    private val countryCode: String,
    private val repository: CountryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CountryDetailUiState(countryCode = countryCode))
    val uiState: StateFlow<CountryDetailUiState> = _uiState.asStateFlow()

    init {
        loadCountry()
    }

    fun onEvent(event: CountryDetailEvent) {
        if (event is CountryDetailEvent.RetryClicked) {
            loadCountry()
        }
    }

    private fun loadCountry() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(content = CountryDetailContentState.Loading)
            }

            runCatching {
                repository.getCountryDetail(countryCode)
            }.onSuccess { country ->
                _uiState.update { currentState ->
                    currentState.copy(
                        title = "Post #${country.id}",
                        content = CountryDetailContentState.Success(
                            country = country.toUiModel(),
                        ),
                    )
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to load country detail for code=$countryCode", throwable)
                _uiState.update { currentState ->
                    currentState.copy(
                        content = CountryDetailContentState.Error(
                            message = throwable.toUserMessage(),
                        ),
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "CountryDetailViewModel"

        fun factory(countryCode: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CountryDetailViewModel(
                    countryCode = countryCode,
                    repository = ServiceLocator.countryRepository,
                )
            }
        }
    }
}

private fun CountryDetail.toUiModel(): CountryDetailBodyUiModel {
    val properties = buildList {
        add(CountryPropertyUiModel(label = "ID поста", value = id.toString()))
        add(CountryPropertyUiModel(label = "ID автора", value = userId.toString()))
    }

    return CountryDetailBodyUiModel(
        title = title,
        body = body,
        properties = properties,
    )
}

private fun Throwable.toUserMessage(): String {
    return when (this) {
        is IOException -> "Проверьте подключение к интернету и попробуйте снова."
        is HttpException -> "Сервер вернул ошибку ${code()}."
        else -> message ?: "Не удалось загрузить детали поста."
    }
}
