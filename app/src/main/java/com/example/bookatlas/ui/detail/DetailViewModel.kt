package com.example.bookatlas.ui.detail

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
import kotlinx.coroutines.launch

data class DetailUiState(
    val book: Book? = null,
    val loading: Boolean = true,
    val error: String? = null
)

sealed interface DetailEvent {
    data object Retry : DetailEvent
    data object ToggleFavorite : DetailEvent
}

private data class DetailNetwork(val loading: Boolean = true, val error: String? = null)

class DetailViewModel(
    private val repository: BooksRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val bookId: Int = checkNotNull(savedStateHandle["bookId"])
    private val network = MutableStateFlow(DetailNetwork())
    val uiState = combine(repository.books, network) { books, status ->
        val book = books.firstOrNull { it.id == bookId }
        DetailUiState(book, loading = status.loading && book == null, error = status.error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState())

    init { refresh() }

    fun onEvent(event: DetailEvent) {
        when (event) {
            DetailEvent.Retry -> refresh()
            DetailEvent.ToggleFavorite -> viewModelScope.launch {
                repository.toggleFavorite(bookId)
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            network.value = DetailNetwork(loading = true)
            val result = repository.refreshBook(bookId)
            network.value = DetailNetwork(
                loading = false,
                error = (result as? RequestResult.Failure)?.message
            )
        }
    }

    companion object {
        fun factory(repository: BooksRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetailViewModel(repository, createSavedStateHandle()) }
        }
    }
}
