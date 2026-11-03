package com.example.bookatlas

import com.example.bookatlas.domain.Book
import com.example.bookatlas.domain.BookMerger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookMergerTest {
    private fun book(id: Int, title: String = "Book", favorite: Boolean = false) =
        Book(id, title, "Author", "Subject", "", "", 12, favorite)

    @Test fun refreshPreservesFavorites() {
        val result = BookMerger.merge(listOf(book(1, "Old", favorite = true)),
            listOf(book(1, "New")), 1000L)
        assertEquals("New", result.single().title)
        assertTrue(result.single().isFavorite)
        assertEquals(1000L, result.single().updatedAt)
    }

    @Test fun offlineEntriesRemainInCache() {
        val result = BookMerger.merge(listOf(book(7)), listOf(book(8)), 1000L)
        assertEquals(setOf(7, 8), result.map { it.id }.toSet())
    }

    @Test fun newBooksAreNotFavoritesByDefault() {
        val result = BookMerger.merge(emptyList(), listOf(book(3)), 1000L)
        assertFalse(result.single().isFavorite)
    }
}
