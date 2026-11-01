package com.example.bookatlas.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.bookatlas.R
import com.example.bookatlas.ui.components.EmptyState
import com.example.bookatlas.ui.components.OfflineBanner

@Composable
fun DetailScreen(viewModel: DetailViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
            Text("О книге", style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            if (state.book != null) {
                IconButton(onClick = { viewModel.onEvent(DetailEvent.ToggleFavorite) }) {
                    Icon(
                        if (state.book!!.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Изменить избранное", tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        when {
            state.loading -> Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
            state.book == null -> EmptyState(
                "Книга недоступна", state.error ?: "Попробуйте открыть карточку снова.",
                onRetry = { viewModel.onEvent(DetailEvent.Retry) }
            )
            else -> {
                val book = state.book!!
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (state.error != null) {
                        OfflineBanner(state.error!!)
                        Spacer(Modifier.height(16.dp))
                    }
                    AsyncImage(
                        model = book.coverUrl.ifBlank { null },
                        contentDescription = "Обложка книги ${book.title}",
                        placeholder = painterResource(R.drawable.book_placeholder),
                        error = painterResource(R.drawable.book_placeholder),
                        fallback = painterResource(R.drawable.book_placeholder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(width = 182.dp, height = 256.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(book.title, style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(book.author, style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Text("О произведении", style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text(book.subjects, style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(14.dp))
                    Text("${book.downloads} загрузок на Project Gutenberg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = {
                        val address = book.readUrl.takeIf { it.startsWith("https://") }
                            ?: "https://www.gutenberg.org/ebooks/${book.id}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
                        try {
                            context.startActivity(intent)
                        } catch (error: ActivityNotFoundException) {
                            Toast.makeText(context, "Не найдено приложение для открытия книги", Toast.LENGTH_LONG).show()
                        } catch (error: SecurityException) {
                            Toast.makeText(context, "Не удалось открыть ссылку на книгу", Toast.LENGTH_LONG).show()
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.OpenInNew, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Читать на Gutenberg")
                    }
                }
            }
        }
    }
}
