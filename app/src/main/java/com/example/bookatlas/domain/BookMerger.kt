package com.example.bookatlas.domain

/** Сохраняет избранное и ранее загруженные книги при обновлении каталога. */
object BookMerger {
    fun merge(cached: List<Book>, incoming: List<Book>, now: Long): List<Book> {
        val saved = cached.associateBy(Book::id)
        val updated = incoming.distinctBy(Book::id).map { remote ->
            remote.copy(
                isFavorite = saved[remote.id]?.isFavorite ?: false,
                updatedAt = now
            )
        }
        val ids = updated.map(Book::id).toHashSet()
        val remaining = cached.filterNot { it.id in ids }
        val merged = updated + remaining
        val favorites = merged.filter(Book::isFavorite)
        val others = merged.filterNot(Book::isFavorite).take((250 - favorites.size).coerceAtLeast(0))
        return favorites + others
    }
}
