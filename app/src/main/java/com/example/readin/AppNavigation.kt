package com.example.readin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.readin.data.local.AppDatabase
import com.example.readin.data.local.BookEntity
import com.example.readin.data.repository.BookRepository
import com.example.readin.screens.BookDetailsScreen
import com.example.readin.screens.DiscoveryDetailsScreen
import com.example.readin.screens.DiscoverScreen
import com.example.readin.screens.LibraryScreen
import com.example.readin.viewmodel.BookViewModel
import com.example.readin.viewmodel.LibraryViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val repository = BookRepository(AppDatabase.getDatabase(context).bookDao())
    val libraryViewModel: LibraryViewModel = viewModel()
    val books by libraryViewModel.books.collectAsState()
    val savedIsbns = remember(books) { books.map { it.isbn }.toSet() }
    var selectedDiscoverBook by remember { mutableStateOf<BookEntity?>(null) }

    //Reference prompt (5)
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

    //Reference Prompt (11)
    NavHost(navController = navController, startDestination = "discover") {
        composable("discover") {
            DiscoverScreen(
                viewModel = bookViewModel,
                savedIsbns = savedIsbns,
                onNavigate = goTo,
                onBookClick = { book ->
                    selectedDiscoverBook = book
                    navController.navigate("discover_details")
                }
            )
        }
        composable("discover_details") {
            val selectedBook = selectedDiscoverBook
            if (selectedBook != null) {
                DiscoveryDetailsScreen(
                    book = selectedBook,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("library") {
            LibraryScreen(
                viewModel = libraryViewModel,
                onNavigate = goTo,
                onBookClick = { book ->
                    navController.navigate("details/${book.isbn}")
                }
            )
        }
        composable(
            route = "details/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.IntType })
        ) { backStackEntry ->
            val isbn = backStackEntry.arguments?.getInt("isbn")
            val selectedBook = books.find { it.isbn == isbn }

            if (selectedBook != null) {
                BookDetailsScreen(
                    book = selectedBook,
                    onBack = { navController.popBackStack() },
                    onRatingChange = { newRating ->
                        libraryViewModel.updateRating(selectedBook, newRating)
                    },
                    onStatusChange = { newStatus ->
                        libraryViewModel.updateStatus(selectedBook, newStatus)
                    },
                    onNotesChange = { newNotes ->
                        libraryViewModel.updateNotes(selectedBook, newNotes)
                    }
                )
            }
        }
    }
}