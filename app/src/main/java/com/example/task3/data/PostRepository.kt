package com.example.task3.data

import com.example.task3.data.model.PostDetail
import com.example.task3.data.model.PostListItem

interface PostRepository {
    suspend fun getPosts(query: String): List<PostListItem>
    suspend fun getPostDetail(postId: String): PostDetail
}
