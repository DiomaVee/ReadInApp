package com.example.readin.screens

//Reference to prompt (2)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.readin.R
import com.example.readin.viewmodel.LibraryViewModel
import androidx.compose.foundation.clickable
import com.example.readin.data.local.BookEntity

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel(),
    onNavigate: (Int) -> Unit = {},
    onBookClick: (BookEntity) -> Unit = {}
) {
    val books by viewModel.books.collectAsState()

    Scaffold(bottomBar = {
        DiscoverBottomBar(
            selectedIndex = 0,
            onNavigate = onNavigate
        )
    }) { padding ->
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBookClick(book) }
                    ) {
                        Box {
                            AsyncImage(
                                model = book.coverUrl,
                                contentDescription = stringResource(R.string.book_cover),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()

                            )
                            FilledTonalIconButton(
                                onClick = { viewModel.deleteBook(book) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(
                                        R.string.delete_book, book.title
                                    )
                                )
                            }
                        }
                        Text(
                            book.title ?: "",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
