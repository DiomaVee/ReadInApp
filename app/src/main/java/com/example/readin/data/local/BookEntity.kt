package com.example.readin.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val isbn: Int = 0,
    val title: String,
    val author: String,
    val genre: String,
    val releaseYear: Int,
    val status: String,
    val rating: Float,
    val notes: String,
    val isPrivate: Boolean = false
)
