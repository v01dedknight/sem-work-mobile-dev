package com.example.bookatlas.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bookatlas.domain.Book
import com.example.bookatlas.domain.BookMerger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.bookDataStore by preferencesDataStore(name = "book_atlas_cache")

class BookCache(context: Context, private val gson: Gson) {
    private val store = context.applicationContext.bookDataStore
    private val booksKey = stringPreferencesKey("cached_books_json")
    private val refreshedKey = longPreferencesKey("last_network_sync")
    private val booksType = object : TypeToken<List<Book>>() {}.type

    private fun decode(json: String?): List<Book> = try {
        if (json.isNullOrBlank()) emptyList()
        else gson.fromJson<List<Book>>(json, booksType)?.filter { it.id > 0 } ?: emptyList()
    } catch (_: RuntimeException) {
        // Повреждённый JSON не должен приводить к падению приложения.
        emptyList()
    }

    private fun encode(books: List<Book>): String = gson.toJson(books)

    private val safePreferences: Flow<Preferences> = store.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    val books: Flow<List<Book>> = safePreferences.map { decode(it[booksKey]) }
    val lastSync: Flow<Long> = safePreferences.map { it[refreshedKey] ?: 0L }

    suspend fun merge(incoming: List<Book>) {
        store.edit { preferences ->
            val now = System.currentTimeMillis()
            val cached = decode(preferences[booksKey])
            preferences[booksKey] = encode(BookMerger.merge(cached, incoming, now))
            preferences[refreshedKey] = now
        }
    }

    suspend fun toggleFavorite(bookId: Int) {
        store.edit { preferences ->
            val cached = decode(preferences[booksKey])
            preferences[booksKey] = encode(cached.map { book ->
                if (book.id == bookId) book.copy(isFavorite = !book.isFavorite) else book
            })
        }
    }
}
