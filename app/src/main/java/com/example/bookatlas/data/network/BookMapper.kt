package com.example.bookatlas.data.network

import com.example.bookatlas.domain.Book

fun BookDto.toDomain(): Book {
    val cover = formats["image/jpeg"].orEmpty().replace("http://", "https://")
    // Используем страницу Project Gutenberg, если ссылка на файл отсутствует.
    val html = formats.entries.firstOrNull {
        it.key.startsWith("text/html") && it.value.startsWith("https://")
    }?.value
    return Book(
        id = id,
        title = title.ifBlank { "Без названия" },
        author = authors.map(AuthorDto::name).filter(String::isNotBlank).joinToString(", ")
            .ifBlank { "Автор неизвестен" },
        subjects = subjects.take(3).joinToString(" • ").ifBlank { "Классическая литература" },
        coverUrl = cover,
        readUrl = html ?: "https://www.gutenberg.org/ebooks/$id",
        downloads = downloadCount
    )
}
