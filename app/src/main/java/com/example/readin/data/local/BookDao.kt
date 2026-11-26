package com.example.readin.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    suspend fun insert(books: BookEntity)

    @Update
    suspend fun update(books: BookEntity)

    @Delete
    suspend fun delete(books: BookEntity)

    @Query("SELECT * FROM books WHERE isPrivate = 0 ORDER BY title ASC")
    fun getPublicBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isPrivate = 1 ORDER BY title ASC")
    fun getPrivateBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isbn = :isbn")
    fun getBook(isbn: Int): Flow<BookEntity?>
}