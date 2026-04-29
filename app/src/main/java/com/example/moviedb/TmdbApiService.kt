package com.example.moviedb

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// Data classes for Reviews
data class Review(
    val author: String,
    val content: String
)

data class ReviewResponse(
    val results: List<Review>
)

// Data classes for Videos
data class Video(
    val name: String,
    val key: String,
    val site: String,
    val type: String
)

data class VideoResponse(
    val results: List<Video>
)

// API interface
interface TmdbApiService {
    @GET("movie/{movieId}/reviews")
    suspend fun getMovieReviews(
        @Path("movieId") movieId: Int,
        @Header("Authorization") token: String
    ): ReviewResponse

    @GET("movie/{movieId}/videos")
    suspend fun getMovieVideos(
        @Path("movieId") movieId: Int,
        @Header("Authorization") token: String
    ): VideoResponse
}

// Retrofit instance
object TmdbApi {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val service: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }
}