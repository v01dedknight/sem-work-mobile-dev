package com.example.bookatlas.data

import com.example.bookatlas.domain.Book
import kotlinx.coroutines.flow.Flow

sealed interface RequestResult {
    data object Success : RequestResult
    data class Failure(val message: String) : RequestResult
}

interface BooksRepository {
    val books: Flow<List<Book>>
    val favorites: Flow<List<Book>>
    val lastSync: Flow<Long>

    suspend fun refresh(query: String = ""): RequestResult
    suspend fun refreshBook(id: Int): RequestResult
    suspend fun toggleFavorite(id: Int)
}
