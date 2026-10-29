package com.example.bookatlas.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookatlas.data.BooksRepository
import com.example.bookatlas.data.RequestResult
import com.example.bookatlas.domain.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUiState(
    val books: List<Book> = emptyList(),
    val query: String = "",
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
    val lastSync: Long = 0L
)

sealed interface CatalogEvent {
    data class QueryChanged(val value: String) : CatalogEvent
    data object Search : CatalogEvent
    data object Refresh : CatalogEvent
    data class ToggleFavorite(val bookId: Int) : CatalogEvent
    data object DismissError : CatalogEvent
}

private data class NetworkStatus(val busy: Boolean = true, val error: String? = null)

fun filterCatalog(books: List<Book>, query: String): List<Book> {
    val term = query.trim()
    if (term.isBlank()) return books.sortedByDescending(Book::downloads)
    return books.filter {
        it.title.contains(term, ignoreCase = true) ||
            it.author.contains(term, ignoreCase = true) ||
            it.subjects.contains(term, ignoreCase = true)
    }.sortedByDescending(Book::downloads)
}

class CatalogViewModel(
    private val repository: BooksRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val query = savedStateHandle.getStateFlow("catalog_query", "")
    private val status = MutableStateFlow(NetworkStatus())

    val uiState = combine(repository.books, query, status, repository.lastSync) {
            books, text, network, timestamp ->
        CatalogUiState(
            books = filterCatalog(books, text),
            query = text,
            loading = network.busy && books.isEmpty(),
            refreshing = network.busy && books.isNotEmpty(),
            error = network.error,
            lastSync = timestamp
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState())

    init { refresh() }

    fun onEvent(event: CatalogEvent) {
        when (event) {
            is CatalogEvent.QueryChanged -> savedStateHandle["catalog_query"] = event.value
            CatalogEvent.Search, CatalogEvent.Refresh -> refresh()
            is CatalogEvent.ToggleFavorite -> viewModelScope.launch {
                repository.toggleFavorite(event.bookId)
            }
            CatalogEvent.DismissError -> status.update { it.copy(error = null) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            status.value = NetworkStatus(busy = true)
            val result = repository.refresh(query.value)
            status.value = NetworkStatus(
                busy = false,
                error = (result as? RequestResult.Failure)?.message
            )
        }
    }

    companion object {
        fun factory(repository: BooksRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { CatalogViewModel(repository, createSavedStateHandle()) }
        }
    }
}
