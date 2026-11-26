package com.example.readin

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.readin.data.local.AppDatabase
import com.example.readin.data.repository.BookRepository
import com.example.readin.screens.DiscoverScreen
import com.example.readin.screens.LibraryScreen
import com.example.readin.viewmodel.BookViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val repository = BookRepository(AppDatabase.getDatabase(context).bookDao())

    val bookViewModel: BookViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookViewModel(repository) as T
            }
        }
    )

    val goTo: (Int) -> Unit = goTo@{ index ->
        val route = when (index) {
            0 -> "library"
            1 -> "discover"
            else -> return@goTo
        }
        navController.navigate(route) {
            popUpTo("discover") { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = "discover") {
        composable("discover") { DiscoverScreen(viewModel = bookViewModel, onNavigate = goTo) }
        composable("library") { LibraryScreen(onNavigate = goTo) }
    }
}