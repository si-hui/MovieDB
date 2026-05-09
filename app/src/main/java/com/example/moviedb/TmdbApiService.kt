package com.example.moviedb

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// Movie list data classes
data class MovieResult(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?
)

data class MovieListResponse(
    val results: List<MovieResult>
)

// Movie detail data classes
data class Genre(
    val id: Int,
    val name: String
)

data class MovieDetailResponse(
    val id: Int,
    val genres: List<Genre>,
    val homepage: String,
    val imdb_id: String
)

// Review data classes
data class Review(
    val author: String,
    val content: String
)

data class ReviewResponse(
    val results: List<Review>
)

// Video data classes
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

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Header("Authorization") token: String
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Header("Authorization") token: String
    ): MovieListResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetail(
        @Path("movieId") movieId: Int,
        @Header("Authorization") token: String
    ): MovieDetailResponse

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

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }
}