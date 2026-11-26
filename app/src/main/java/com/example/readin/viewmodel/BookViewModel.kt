package com.example.readin.viewmodel

//Reference prompt (1)
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readin.data.local.BookDao
import com.example.readin.data.local.BookEntity
import com.example.readin.data.remote.BookItem
import com.example.readin.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _results = MutableStateFlow<List<BookItem>>(emptyList())
    val results: StateFlow<List<BookItem>> = _results

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _searched = MutableStateFlow(false)
    val searched: StateFlow<Boolean> = _searched

    fun searchBooks(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            _results.value = try {
                repository.search(query).items ?: emptyList()
            } catch (e: Exception) {
                Log.e("BookSearch", "Search failed", e)
                emptyList()
            }
            _searched.value = true
            _loading.value = false
        }
    }

    //Reference prompt (5)
    fun saveBook(item: BookItem) {
        val info = item.volumeInfo
        val year = info.publishedDate
            ?.take(4)
            ?.toIntOrNull()
            ?: 0

        val entity = BookEntity(
            isbn = 0,
            title = info.title ?: "Untitled",
            author = info.authors?.joinToString(", ") ?: "Unknown Author",
            genre = info.categories?.firstOrNull() ?: "General",
            releaseYear = year,
            status = "To Read",
            rating = 0f,
            notes = "",
            isPrivate = false
        )

        viewModelScope.launch {
            try {
                repository.saveBook(entity)
            } catch (e: Exception) {
                Log.e("BookSave", "Failed to save book", e)
            }
        }
    }
}