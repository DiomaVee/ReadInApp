package com.example.readin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readin.data.remote.BookItem
import com.example.readin.data.local.AppDatabase
import com.example.readin.data.local.BookEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).bookDao()

    val books: StateFlow<List<BookEntity>> = dao.getPublicBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // TEMPORARY: just so you can see the list working
    fun addTestBook() {
        viewModelScope.launch {
            dao.insert(
                BookEntity(
                    isbn = System.currentTimeMillis().toInt(),
                    title = "Test Book",
                    author = "Test Author",
                    genre = "Fantasy",
                    releaseYear = 2020,
                    status = "Reading",
                    rating = 4f,
                    notes = ""
                )
            )
        }
    }
}
