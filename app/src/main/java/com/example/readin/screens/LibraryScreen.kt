package com.example.readin.screens

//Reference to prompt (2)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readin.R
import com.example.readin.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel(),
    onNavigate: (Int) -> Unit = {}
) {
    val books by viewModel.books.collectAsState()

    Scaffold(bottomBar = { DiscoverBottomBar(selectedIndex = 0, onNavigate = onNavigate) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            Text(
                stringResource(R.string.library_title),
                style = MaterialTheme.typography.headlineMedium
            )


            if (books.isEmpty()) {
                Text(stringResource(R.string.empty_library))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(books, key = { it.isbn }) { book ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(book.title, style = MaterialTheme.typography.titleMedium)
                            Text(book.author)
                        }
                    }
                }
            }
        }
    }
}