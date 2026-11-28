//Refence prompt (1)
package com.example.readin.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.readin.BuildConfig

data class BooksResponse(val items: List<BookItem>? = null)
data class BookItem(val id: String, val volumeInfo: VolumeInfo)
data class VolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val publishedDate: String? = null,
    val categories: List<String>? = null,
    val imageLinks: ImageLinks? = null,
    val industryIdentifiers: List<Identifier>? = null,
    val averageRating: Double? = null,
    val description: String? = null
)
data class ImageLinks(val thumbnail: String? = null)
data class Identifier(val type: String, val identifier: String)

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun search(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") key: String = BuildConfig.BOOKS_API_KEY
    ): BooksResponse

}