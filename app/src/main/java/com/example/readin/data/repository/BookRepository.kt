
package com.example.readin.data.repository

import com.example.readin.data.local.BookDao
import com.example.readin.data.local.BookEntity
import com.example.readin.data.remote.BooksResponse
import com.example.readin.data.remote.RetrofitInstance

class BookRepository(private val bookDao: BookDao) {
    suspend fun search(query: String): BooksResponse =
        RetrofitInstance.api.search(query)

    suspend fun saveBook(book: BookEntity){
        bookDao.insert(book)
    }
}