package com.example.task3.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PostDetailRoute(
    postId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = viewModel(
        factory = PostDetailViewModel.factory(postId),
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PostDetailScreen(
        state = state,
        modifier = modifier,
        onEvent = { event ->
            when (event) {
                PostDetailEvent.BackClicked -> onBack()
                PostDetailEvent.RetryClicked -> viewModel.onEvent(event)
            }
        },
    )
}
