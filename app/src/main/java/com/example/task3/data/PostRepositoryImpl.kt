package com.example.task3.data

import com.example.task3.data.model.PostDetail
import com.example.task3.data.model.PostListItem
import com.example.task3.data.remote.PostApiService
import com.example.task3.data.remote.dto.toPostDetail
import com.example.task3.data.remote.dto.toPostListItemOrNull

class PostRepositoryImpl(
    private val apiService: PostApiService,
) : PostRepository {

    override suspend fun getPosts(query: String): List<PostListItem> {
        val posts = if (query.isBlank()) {
            apiService.getAllPosts()
        } else {
            query.trim().toIntOrNull()?.let { userId ->
                apiService.getPostsByUserId(userId = userId)
            }.orEmpty()
        }

        return posts
            .mapNotNull { it.toPostListItemOrNull() }
            .sortedBy { it.id }
    }

    override suspend fun getPostDetail(postId: String): PostDetail {
        val id = postId.trim().toIntOrNull()
            ?: throw IllegalStateException("Некорректный идентификатор поста: $postId")

        return apiService.getPostDetail(id).toPostDetail()
    }
}
