package com.example.task3.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class PostDetail(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
)
