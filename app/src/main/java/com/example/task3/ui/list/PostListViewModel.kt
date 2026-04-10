package com.example.task3.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.task3.data.PostRepository
import com.example.task3.data.ServiceLocator
import com.example.task3.data.model.PostListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostListViewModel(
    private val repository: PostRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState: StateFlow<PostListUiState> = _uiState.asStateFlow()

    private var lastSubmittedQuery: String = ""

    init {
        loadPosts(query = "")
    }

    fun onEvent(event: PostListEvent) {
        when (event) {
            is PostListEvent.QueryChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(searchQuery = event.value)
                }
            }

            PostListEvent.SearchSubmitted -> {
                val query = _uiState.value.searchQuery.trim()
                lastSubmittedQuery = query
                if (query.isNotBlank() && query.toIntOrNull() !in 1..10) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            content = PostListContentState.Empty(
                                title = "Некорректный фильтр",
                                message = "Введите userId числом от 1 до 10.",
                            ),
                        )
                    }
                } else {
                    loadPosts(query = query)
                }
            }

            PostListEvent.ClearSearchClicked -> {
                lastSubmittedQuery = ""
                _uiState.update { currentState ->
                    currentState.copy(searchQuery = "")
                }
                loadPosts(query = "")
            }

            PostListEvent.RetryClicked -> {
                if (lastSubmittedQuery.isNotBlank() && lastSubmittedQuery.toIntOrNull() !in 1..10) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            content = PostListContentState.Empty(
                                title = "Некорректный фильтр",
                                message = "Введите userId числом от 1 до 10.",
                            ),
                        )
                    }
                } else {
                    loadPosts(query = lastSubmittedQuery)
                }
            }

            is PostListEvent.PostClicked -> Unit
        }
    }

    private fun loadPosts(query: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(content = PostListContentState.Loading)
            }

            runCatching {
                repository.getPosts(query)
            }.onSuccess { posts ->
                _uiState.update { currentState ->
                    currentState.copy(content = posts.toContentState(query = query))
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to load posts", throwable)
                _uiState.update { currentState ->
                    currentState.copy(
                        content = PostListContentState.Error(
                            message = throwable.toUserMessage(),
                        ),
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "PostListViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PostListViewModel(repository = ServiceLocator.postRepository)
            }
        }
    }
}

private fun List<PostListItem>.toContentState(query: String): PostListContentState {
    if (isEmpty()) {
        return PostListContentState.Empty(
            title = "Ничего не найдено",
            message = if (query.isBlank()) {
                "API вернул пустой список постов."
            } else {
                "По userId=$query ничего не найдено."
            },
        )
    }

    val summary = if (query.isBlank()) {
        "Найдено постов: $size"
    } else {
        "Найдено постов для userId=$query: $size"
    }

    return PostListContentState.Success(
        summary = summary,
        items = map { post ->
            PostCardUiModel(
                id = post.id.toString(),
                title = post.title,
                subtitle = "Post #${post.id} · User #${post.userId}",
                bodyPreview = post.preview,
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
