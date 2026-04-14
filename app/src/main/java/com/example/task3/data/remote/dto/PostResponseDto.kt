package com.example.task3.data.remote.dto

import com.example.task3.data.model.PostDetail
import com.example.task3.data.model.PostListItem
import com.google.gson.annotations.SerializedName

data class PostResponseDto(
    @SerializedName("userId") val userId: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
)

fun PostResponseDto.toPostListItemOrNull(): PostListItem? {
    val postId = id ?: return null
    val ownerId = userId ?: return null

    return PostListItem(
        id = postId,
        userId = ownerId,
        title = title.orFallback("Без названия"),
        preview = body
            .orFallback("Без описания")
            .replace("\n", " ")
            .trim(),
    )
}

fun PostResponseDto.toPostDetail(): PostDetail {
    val postId = id ?: throw IllegalStateException("В ответе сервера отсутствует id поста.")
    val ownerId = userId ?: throw IllegalStateException("В ответе сервера отсутствует userId поста.")

    return PostDetail(
        id = postId,
        userId = ownerId,
        title = title.orFallback("Без названия"),
        body = body.orFallback("Без описания"),
    )
}

private fun String?.orFallback(fallback: String): String {
    return this?.takeIf { it.isNotBlank() } ?: fallback
}
