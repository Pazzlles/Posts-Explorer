package com.example.task3.data.remote

import com.example.task3.data.remote.dto.PostResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {
    @GET("posts")
    suspend fun getAllPosts(): List<PostResponseDto>

    @GET("posts")
    suspend fun getPostsByUserId(
        @Query("userId") userId: Int,
    ): List<PostResponseDto>

    @GET("posts/{id}")
    suspend fun getPostDetail(
        @Path("id") id: Int,
    ): PostResponseDto
}
