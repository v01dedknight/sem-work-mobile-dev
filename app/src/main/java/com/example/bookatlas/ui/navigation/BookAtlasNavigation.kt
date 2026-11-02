package com.example.bookatlas.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookatlas.data.BooksRepository
import com.example.bookatlas.ui.catalog.CatalogScreen
import com.example.bookatlas.ui.catalog.CatalogViewModel
import com.example.bookatlas.ui.detail.DetailScreen
import com.example.bookatlas.ui.detail.DetailViewModel
import com.example.bookatlas.ui.favorites.FavoritesScreen
import com.example.bookatlas.ui.favorites.FavoritesViewModel

private const val CATALOG = "catalog"
private const val FAVORITES = "favorites"
private const val DETAIL = "detail/{bookId}"

@Composable
fun BookAtlasNavigation(repository: BooksRepository) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (route == CATALOG || route == FAVORITES) {
                NavigationBar {
                    NavigationBarItem(
                        selected = route == CATALOG,
                        onClick = {
                            navController.navigate(CATALOG) {
                                popUpTo(CATALOG) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Каталог") }
                    )
                    NavigationBarItem(
                        selected = route == FAVORITES,
                        onClick = {
                            navController.navigate(FAVORITES) {
                                popUpTo(CATALOG) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text("Избранное") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = CATALOG, modifier = Modifier.padding(innerPadding)) {
            composable(CATALOG) { entry ->
                val vm: CatalogViewModel = viewModel(
                    viewModelStoreOwner = entry,
                    factory = CatalogViewModel.factory(repository)
                )
                CatalogScreen(vm, onOpenBook = { navController.navigate("detail/$it") })
            }
            composable(FAVORITES) { entry ->
                val vm: FavoritesViewModel = viewModel(
                    viewModelStoreOwner = entry,
                    factory = FavoritesViewModel.factory(repository)
                )
                FavoritesScreen(vm, onOpenBook = { navController.navigate("detail/$it") })
            }
            composable(DETAIL, arguments = listOf(navArgument("bookId") {
                type = NavType.IntType
            })) { entry ->
                val vm: DetailViewModel = viewModel(
                    viewModelStoreOwner = entry,
                    factory = DetailViewModel.factory(repository)
                )
                DetailScreen(vm, onBack = { navController.popBackStack() })
            }
        }
    }
}
