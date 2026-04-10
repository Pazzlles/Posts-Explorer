package com.example.task3.data.remote.dto

import com.example.task3.data.model.CountryDetail
import com.example.task3.data.model.CountryListItem
import com.google.gson.annotations.SerializedName

data class CountryResponseDto(
    @SerializedName("userId") val userId: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
)

fun CountryResponseDto.toCountryListItemOrNull(): CountryListItem? {
    val postId = id ?: return null
    val ownerId = userId ?: return null

    return CountryListItem(
        id = postId,
        userId = ownerId,
        title = title.orFallback("Без названия"),
        preview = body
            .orFallback("Без описания")
            .replace("\n", " ")
            .trim(),
    )
}

fun CountryResponseDto.toCountryDetail(): CountryDetail {
    return CountryDetail(
        id = id ?: 0,
        userId = userId ?: 0,
        title = title.orFallback("Без названия"),
        body = body.orFallback("Без описания"),
    )
}

private fun String?.orFallback(fallback: String): String {
    return this?.takeIf { it.isNotBlank() } ?: fallback
}
