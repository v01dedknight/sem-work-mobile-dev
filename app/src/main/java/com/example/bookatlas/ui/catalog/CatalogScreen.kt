package com.example.bookatlas.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookatlas.ui.components.BookCard
import com.example.bookatlas.ui.components.EmptyState
import com.example.bookatlas.ui.components.OfflineBanner

@Composable
fun CatalogScreen(viewModel: CatalogViewModel, onOpenBook: (Int) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focus = LocalFocusManager.current
    var showSuggestions by rememberSaveable { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("BOOK ATLAS", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Книги, которые остаются", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold)
                Text("Открывайте классику. Сохраняйте любимое.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { viewModel.onEvent(CatalogEvent.Refresh) }) {
                Icon(Icons.Default.Refresh, contentDescription = "Обновить каталог")
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.onEvent(CatalogEvent.QueryChanged(it)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Название или автор") },
            placeholder = { Text("Например, Sherlock Holmes") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.onEvent(CatalogEvent.Search)
                focus.clearFocus()
            }),
            trailingIcon = {
                androidx.compose.material3.TextButton(onClick = {
                    viewModel.onEvent(CatalogEvent.Search)
                    focus.clearFocus()
                }) { Text("Найти") }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (showSuggestions && state.query.isBlank()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
            ) {
                listOf("Fiction", "Adventure", "History").forEach { suggestion ->
                    AssistChip(onClick = {
                        viewModel.onEvent(CatalogEvent.QueryChanged(suggestion))
                        viewModel.onEvent(CatalogEvent.Search)
                        showSuggestions = false
                    }, label = { Text(suggestion) })
                }
            }
        }
        if (state.refreshing) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            OfflineBanner(state.error!!)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            if (state.query.isBlank()) "Популярные книги · ${state.books.size}"
            else "Результаты · ${state.books.size}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        when {
            state.loading -> {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Загружаем каталог…")
                }
            }
            state.books.isEmpty() -> EmptyState(
                title = if (state.query.isBlank()) "Каталог пока пуст" else "Ничего не найдено",
                message = if (state.query.isBlank())
                    "Подключитесь к интернету и обновите список."
                else "Попробуйте другой запрос. При наличии сети нажмите «Найти».",
                onRetry = { viewModel.onEvent(CatalogEvent.Refresh) }
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.books, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        onClick = { onOpenBook(book.id) },
                        onFavorite = { viewModel.onEvent(CatalogEvent.ToggleFavorite(book.id)) }
                    )
                }
            }
        }
    }
}
