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

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Header("Authorization") token: String
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Header("Authorization") token: String
    ): MovieListResponse
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

data class ApiMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,   // snake_case to match API
    val backdrop_path: String?,
    val vote_average: Double,
    val genre_ids: List<Int>
)

data class MovieListResponse(
    val results: List<ApiMovie>
)