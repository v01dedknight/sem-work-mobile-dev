package com.example.bookatlas

import com.example.bookatlas.domain.Book
import com.example.bookatlas.ui.catalog.filterCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogFilterTest {
    private fun book(id: Int, title: String, author: String) =
        Book(id, title, author, "Fiction", "", "", 100)

    @Test fun searchesTitleAndAuthorIgnoringCase() {
        val books = listOf(book(1, "The Hobbit", "Tolkien"), book(2, "Pride", "Jane Austen"))
        assertEquals(1, filterCatalog(books, "HOBBIT").single().id)
        assertEquals(2, filterCatalog(books, "austen").single().id)
    }

    @Test fun blankQueryShowsWholeCache() {
        assertEquals(2, filterCatalog(listOf(book(1,"A","B"), book(2,"C","D")), "  ").size)
    }
}
