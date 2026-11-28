package com.example.readin.screens

//Reference to prompt (1)
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import com.example.readin.data.local.BookEntity
import com.example.readin.data.remote.BookItem
import com.example.readin.data.remote.RetrofitInstance.api
import com.example.readin.viewmodel.BookViewModel

@Composable
fun DiscoverScreen(
    viewModel: BookViewModel = viewModel(),
    savedIsbns: Set<Int> = emptySet(),
    onNavigate: (Int) -> Unit = {},
    onBookClick: (BookEntity) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.results.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val searched by viewModel.searched.collectAsState()

    //Reference Prompt (10)
    var horrorBooks by remember { mutableStateOf<List<BookItem>>(emptyList()) }
    var fantasyBooks by remember { mutableStateOf<List<BookItem>>(emptyList()) }
    var crimeBooks by remember { mutableStateOf<List<BookItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = api.search(query = "subject:Horror", maxResults = 20)
            horrorBooks = response.items
                .orEmpty()
                .sortedByDescending { it.volumeInfo.averageRating ?: 0.0 }
        } catch (_: Exception) { }

        try {
            val response = api.search(query = "subject:Fantasy", maxResults = 20)
            fantasyBooks = response.items
                .orEmpty()
                .sortedByDescending { it.volumeInfo.averageRating ?: 0.0 }
        } catch (_: Exception) { }

        try {
            val response = api.search(query = "subject:Crime", maxResults = 20)
            crimeBooks = response.items
                .orEmpty()
                .sortedByDescending { it.volumeInfo.averageRating ?: 0.0 }
        } catch (_: Exception) { }
    }

    Scaffold(bottomBar = { DiscoverBottomBar(selectedIndex = 1, onNavigate = onNavigate) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            if (loading) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }

            //Reference Prompt (10)
            if (!searched && horrorBooks.isNotEmpty()) {
                item {
                    Text(
                        text = "Top Rated Horror",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(horrorBooks, key = { index, book -> "horror_${book.id}_$index" }) { _, book ->
                            val entity = remember(book) { book.toBookEntity() }
                            DiscoverCarouselCard(
                                book = book,
                                entity = entity,
                                isSaved = entity.isbn in savedIsbns,
                                onAddClick = { viewModel.saveBook(book) },
                                onBookClick = onBookClick
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (!searched && fantasyBooks.isNotEmpty()) {
                item {
                    Text(
                        text = "Top Rated Fantasy",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(fantasyBooks, key = { index, book -> "fantasy_${book.id}_$index" }) { _, book ->
                            val entity = remember(book) { book.toBookEntity() }
                            DiscoverCarouselCard(
                                book = book,
                                entity = entity,
                                isSaved = entity.isbn in savedIsbns,
                                onAddClick = { viewModel.saveBook(book) },
                                onBookClick = onBookClick
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (!searched && crimeBooks.isNotEmpty()) {
                item {
                    Text(
                        text = "Top Rated Crime",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(crimeBooks, key = { index, book -> "crime_${book.id}_$index" }) { _, book ->
                            val entity = remember(book) { book.toBookEntity() }
                            DiscoverCarouselCard(
                                book = book,
                                entity = entity,
                                isSaved = entity.isbn in savedIsbns,
                                onAddClick = { viewModel.saveBook(book) },
                                onBookClick = onBookClick
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (searched && results.isEmpty() && !loading) {
                item {
                    Text(
                        text = stringResource(R.string.no_results),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // 3-column search results inside LazyColumn
            val resultRows = results.chunked(3)
            itemsIndexed(resultRows, key = { rowIndex, _ -> "search_row_$rowIndex" }) { _, rowBooks ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    for (colIndex in 0 until 3) {
                        val book = rowBooks.getOrNull(colIndex)
                        if (book != null) {
                            val info = book.volumeInfo
                            val entity = remember(book) { book.toBookEntity() }
                            val isSaved = entity.isbn in savedIsbns
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onBookClick(entity) }
                            ) {
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
                                            imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = if (isSaved) {
                                                "Saved ${info.title ?: ""}"
                                            } else {
                                                stringResource(R.string.add_book, info.title ?: "")
                                            }
                                        )
                                    }
                                }
                                Text(
                                    text = info.title ?: "",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverCarouselCard(
    book: BookItem,
    entity: BookEntity,
    isSaved: Boolean,
    onAddClick: () -> Unit,
    onBookClick: (BookEntity) -> Unit
) {
    val info = book.volumeInfo
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onBookClick(entity) }
    ) {
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
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (isSaved) {
                        "Saved ${info.title ?: ""}"
                    } else {
                        stringResource(R.string.add_book, info.title ?: "")
                    }
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

//Reference prompt (11)
private fun BookItem.toBookEntity(): BookEntity {
    val info = volumeInfo
    val isbnInt = info.industryIdentifiers
        ?.firstOrNull { it.type == "ISBN_13" || it.type == "ISBN_10" }
        ?.identifier
        ?.filter { it.isDigit() }
        ?.toIntOrNull()
        ?: id.hashCode()
    val year = info.publishedDate
        ?.take(4)
        ?.toIntOrNull()
        ?: 0

    return BookEntity(
        isbn = isbnInt,
        title = info.title ?: "Untitled",
        author = info.authors?.joinToString(", ") ?: "Unknown Author",
        genre = info.categories?.firstOrNull() ?: "General",
        releaseYear = year,
        status = "To Read",
        rating = 0f,
        googleRating = info.averageRating,
        notes = "",
        isPrivate = false,
        coverUrl = info.imageLinks?.thumbnail?.replace("http://", "https://"),
        description = info.description ?: ""
    )
}