package com.example.readin.viewmodel

import android.app.Application
import android.util.Log
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

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            try{
                dao.delete(book)
            }
            catch (e: Exception){
                Log.e("BookDelete", "Failed to delete book", e)
            }
        }
    }

    fun updateRating(book: BookEntity, newRating: Float) {
        viewModelScope.launch {
            try{
                if(book.status == "Finished") {
                    dao.update(book.copy(rating = newRating))
                }
                else {
                    Log.e("LibraryViewModel", "Cannot update rating for unfinished book")
                }
            }
            catch(e: Exception){
                Log.e("LibraryViewModel", "Failed to update rating", e)
            }
        }
    }

    fun updateStatus(book: BookEntity, newStatus: String) {
        viewModelScope.launch {
            try {
                dao.update(book.copy(status = newStatus))
            } catch (e: Exception) {
                Log.e("LibraryViewModel", "Failed to update status", e)
            }
        }
    }

    fun updateNotes(book: BookEntity, newNotes: String) {
        viewModelScope.launch {
            try {
                dao.update(book.copy(notes = newNotes))
            } catch (e: Exception) {
                Log.e("LibraryViewModel", "Failed to update notes", e)
            }
        }
    }

}
