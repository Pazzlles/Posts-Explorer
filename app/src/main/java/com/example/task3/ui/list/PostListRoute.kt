package com.example.task3.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PostListRoute(
    onOpenPost: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostListViewModel = viewModel(factory = PostListViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PostListScreen(
        state = state,
        modifier = modifier,
        onEvent = { event ->
            when (event) {
                is PostListEvent.PostClicked -> onOpenPost(event.postId)
                else -> viewModel.onEvent(event)
            }
        },
    )
}
