package com.example.bookatlas.domain

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val subjects: String,
    val coverUrl: String,
    val readUrl: String,
    val downloads: Int,
    val isFavorite: Boolean = false,
    val updatedAt: Long = 0L
)
