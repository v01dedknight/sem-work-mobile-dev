package com.example.bookatlas.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookatlas.ui.components.BookCard
import com.example.bookatlas.ui.components.EmptyState

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel, onOpenBook: (Int) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(20.dp))
        Text("Моя коллекция", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold)
        Text("Избранное доступно даже без подключения к сети.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        if (state.books.isEmpty()) {
            EmptyState("Пока нет сохранённых книг", "Откройте каталог и нажмите на сердечко у понравившейся книги.")
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.books, key = { it.id }) { book ->
                    BookCard(book, onClick = { onOpenBook(book.id) },
                        onFavorite = { viewModel.onEvent(FavoritesEvent.ToggleFavorite(book.id)) })
                }
            }
        }
    }
}
