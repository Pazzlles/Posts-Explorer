package com.example.task3.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.task3.data.PostRepository
import com.example.task3.data.ServiceLocator
import com.example.task3.data.model.PostDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostDetailViewModel(
    private val postId: String,
    private val repository: PostRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState(postId = postId))
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        loadPost()
    }

    fun onEvent(event: PostDetailEvent) {
        if (event is PostDetailEvent.RetryClicked) {
            loadPost()
        }
    }

    private fun loadPost() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(content = PostDetailContentState.Loading)
            }

            runCatching {
                repository.getPostDetail(postId)
            }.onSuccess { post ->
                _uiState.update { currentState ->
                    currentState.copy(
                        title = "Post #${post.id}",
                        content = PostDetailContentState.Success(
                            post = post.toUiModel(),
                        ),
                    )
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to load post detail for id=$postId", throwable)
                _uiState.update { currentState ->
                    currentState.copy(
                        content = PostDetailContentState.Error(
                            message = throwable.toUserMessage(),
                        ),
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "PostDetailViewModel"

        fun factory(postId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PostDetailViewModel(
                    postId = postId,
                    repository = ServiceLocator.postRepository,
                )
            }
        }
    }
}

private fun PostDetail.toUiModel(): PostDetailBodyUiModel {
    val properties = buildList {
        add(PostPropertyUiModel(label = "ID поста", value = id.toString()))
        add(PostPropertyUiModel(label = "ID автора", value = userId.toString()))
    }

    return PostDetailBodyUiModel(
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
