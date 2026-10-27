package com.example.bookatlas.data

import android.content.Context
import com.example.bookatlas.data.local.BookCache
import com.example.bookatlas.data.network.GutendexApi
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private val api = Retrofit.Builder()
        .baseUrl("https://gutendex.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(GutendexApi::class.java)

    val booksRepository: BooksRepository = DefaultBooksRepository(api, BookCache(context, gson))
}
