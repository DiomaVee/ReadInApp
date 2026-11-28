package com.example.readin.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.readin.R
import com.example.readin.data.local.BookEntity
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow

private val readingStatuses = listOf("To Read", "Reading", "Finished")
//Reference prompt (6)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    book: BookEntity,
    onBack: () -> Unit = {},
    onRatingChange: (Float) -> Unit = {},
    onStatusChange: (String) -> Unit = {},
    onNotesChange: (String) -> Unit = {}
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.book_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = stringResource(R.string.book_cover),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(180.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = book.author,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            //Reference Prompt (7)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = if (book.releaseYear > 0) {
                            stringResource(R.string.release_year_value, book.releaseYear)
                        } else {
                            stringResource(R.string.unknown_year)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        book.googleRating?.let {
                            Text(
                                text = stringResource(R.string.rating_value, it),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            //Reference Prompt (8)
            var statusMenuExpanded by remember { mutableStateOf(false) }
            var isEditing by remember { mutableStateOf(false) }

            if(isEditing) {

                Text(
                    text = stringResource(R.string.reading_status),
                    style = MaterialTheme.typography.titleSmall
                )

                ExposedDropdownMenuBox(
                    expanded = statusMenuExpanded,
                    onExpandedChange = { statusMenuExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = book.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.reading_status)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        readingStatuses.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption) },
                                onClick = {
                                    onStatusChange(statusOption)
                                    statusMenuExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                //Reference Prompt (9)
                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.your_rating),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentStars = book.rating.toInt().coerceIn(0, 5)
                    for (star in 1..5) {
                        IconButton(
                            onClick = { onRatingChange(star.toFloat()) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= currentStars) {
                                    Icons.Default.Star
                                } else {
                                    Icons.Outlined.Star
                                },
                                contentDescription = stringResource(R.string.set_rating_stars, star),
                                tint = if (star <= currentStars) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.rating_value, book.rating),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                var notesText by remember(book.isbn, book.notes) { mutableStateOf(book.notes) }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.notes),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text(stringResource(R.string.notes)) },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { onNotesChange(notesText) },
                    enabled = notesText != book.notes,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.save))
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { isEditing = false },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel Editing")
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    var isExpanded by remember(book.isbn) { mutableStateOf(false) }
                    var hasOverflow by remember(book.isbn) { mutableStateOf(false) }

                    Text(
                        text = book.description ?: "No description available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 10,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { textLayoutResult ->
                            if (!isExpanded) {
                                hasOverflow = textLayoutResult.hasVisualOverflow
                            }
                        },
                        modifier = Modifier.align(Alignment.Start)
                    )

                    if (hasOverflow || isExpanded) {
                        Text(
                            text = if (isExpanded) "Show less" else "See more",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp)
                                .clickable { isExpanded = !isExpanded }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Edit Details")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailsScreenPreview() {
    MaterialTheme {
        BookDetailsScreen(
            book = BookEntity(
                isbn = 123456,
                title = "The Left Hand of Darkness",
                author = "Ursula K. Le Guin",
                genre = "Sci-Fi",
                releaseYear = 1969,
                status = "Reading",
                rating = 4.5f,
                notes = "Great world-building and pacing.",
                isPrivate = false,
                coverUrl = null
            )
        )
    }
}