package com.example.bookatlas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.bookatlas.ui.navigation.BookAtlasNavigation
import com.example.bookatlas.ui.theme.BookAtlasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val repository = (application as BookAtlasApplication).container.booksRepository
        setContent {
            BookAtlasTheme {
                BookAtlasNavigation(repository)
            }
        }
    }
}
