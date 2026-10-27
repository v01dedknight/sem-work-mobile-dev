package com.example.bookatlas.data

import com.example.bookatlas.data.local.BookCache
import com.example.bookatlas.data.network.GutendexApi
import com.example.bookatlas.data.network.toDomain
import com.example.bookatlas.domain.Book
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class DefaultBooksRepository(
    private val api: GutendexApi,
    private val cache: BookCache
) : BooksRepository {
    override val books: Flow<List<Book>> = cache.books
    override val favorites: Flow<List<Book>> = books.map { list -> list.filter(Book::isFavorite) }
    override val lastSync: Flow<Long> = cache.lastSync

    override suspend fun refresh(query: String): RequestResult = request {
        val remote = api.getBooks(query.trim().ifBlank { null })
        cache.merge(remote.results.map { it.toDomain() })
    }

    override suspend fun refreshBook(id: Int): RequestResult = request {
        cache.merge(listOf(api.getBook(id).toDomain()))
    }

    override suspend fun toggleFavorite(id: Int) = cache.toggleFavorite(id)

    private suspend fun request(block: suspend () -> Unit): RequestResult = try {
        block()
        RequestResult.Success
    } catch (error: CancellationException) {
        throw error
    } catch (error: HttpException) {
        RequestResult.Failure("Ошибка сервера: HTTP ${error.code()}")
    } catch (_: IOException) {
        RequestResult.Failure("Нет соединения. Показываем сохранённые книги.")
    } catch (_: Exception) {
        RequestResult.Failure("Не удалось обновить каталог. Попробуйте позже.")
    }
}
