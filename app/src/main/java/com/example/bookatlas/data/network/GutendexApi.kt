package com.example.bookatlas.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GutendexApi {
    @GET("books/")
    suspend fun getBooks(@Query("search") search: String? = null): BooksPageDto

    @GET("books/{id}/")
    suspend fun getBook(@Path("id") bookId: Int): BookDto
}

data class BooksPageDto(val results: List<BookDto> = emptyList())
data class AuthorDto(val name: String = "")
data class BookDto(
    val id: Int,
    val title: String,
    val authors: List<AuthorDto> = emptyList(),
    val subjects: List<String> = emptyList(),
    val formats: Map<String, String> = emptyMap(),
    @SerializedName("download_count") val downloadCount: Int = 0
)
