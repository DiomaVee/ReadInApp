package com.example.readin.screens

//Reference to prompt (1)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.readin.R
import com.example.readin.data.remote.BookItem
import com.example.readin.data.remote.RetrofitInstance.api
import com.example.readin.viewmodel.BookViewModel

@Composable
fun DiscoverScreen(
    viewModel: BookViewModel = viewModel(),
    onNavigate: (Int) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.results.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val searched by viewModel.searched.collectAsState()

    //Reference Prompt (10)
    var horrorBooks by remember { mutableStateOf<List<BookItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = api.search(query = "subject:Horror", maxResults = 20)
            horrorBooks = response.items
                .orEmpty()
                .sortedByDescending { it.volumeInfo.averageRating ?: 0.0 }
        } catch (_: Exception) { }
    }

    Scaffold(bottomBar = { DiscoverBottomBar(selectedIndex = 1, onNavigate = onNavigate) }) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {

            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(R.string.search)) },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchBooks(query) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.searchBooks(query) }),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            //Reference Prompt (10)
            if (!searched && horrorBooks.isNotEmpty()) {
                Text(
                    text = "Top Rated Horror",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(horrorBooks, key = { index, book -> "${book.id}_$index" }) { _, book ->
                        val info = book.volumeInfo
                        Column(Modifier.width(120.dp)) {
                            Box {
                                AsyncImage(
                                    model = info.imageLinks?.thumbnail?.replace("http://", "https://"),
                                    contentDescription = stringResource(R.string.book_cover),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f / 3f)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                FilledTonalIconButton(
                                    onClick = { viewModel.saveBook(book) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = stringResource(R.string.add_book, info.title ?: "")
                                    )
                                }
                            }
                            Text(
                                text = info.title ?: "",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            if (searched && results.isEmpty() && !loading) {
                Text(stringResource(R.string.no_results))
            }

            // Grid of book covers
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(results, key = { it.id }) { book ->
                    val info = book.volumeInfo
                    Column {
                        Box {
                            AsyncImage(
                                model = info.imageLinks?.thumbnail?.replace("http://", "https://"),
                                contentDescription = stringResource(R.string.book_cover),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            FilledTonalIconButton(
                                onClick = { viewModel.saveBook(book) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(
                                        R.string.add_book, info.title ?: ""
                                    )
                                )
                            }
                        }
                        Text(
                            info.title ?: "",
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


@Composable
fun DiscoverBottomBar(selectedIndex: Int = 1, onNavigate: (Int) -> Unit = {}) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex == 0, onClick = { onNavigate(0) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text(stringResource(R.string.library)) }
        )
        NavigationBarItem(
            selected = selectedIndex == 1, onClick = { onNavigate(1) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.discover)) }
        )
        NavigationBarItem(
            selected = selectedIndex == 2, onClick = { onNavigate(2) },
            icon = { Icon(Icons.Default.Lock, contentDescription = null) },
            label = { Text(stringResource(R.string.private_library)) }
        )
    }
}