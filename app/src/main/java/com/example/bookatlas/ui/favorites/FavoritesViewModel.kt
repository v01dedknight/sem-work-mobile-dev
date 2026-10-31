package com.example.bookatlas.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookatlas.data.BooksRepository
import com.example.bookatlas.domain.Book
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoritesUiState(val books: List<Book> = emptyList())

sealed interface FavoritesEvent {
    data class ToggleFavorite(val bookId: Int) : FavoritesEvent
}

class FavoritesViewModel(private val repository: BooksRepository) : ViewModel() {
    val uiState = repository.favorites.map { FavoritesUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.ToggleFavorite -> viewModelScope.launch {
                repository.toggleFavorite(event.bookId)
            }
        }
    }

    companion object {
        fun factory(repository: BooksRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { FavoritesViewModel(repository) }
        }
    }
}
