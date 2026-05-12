package com.example.moviedb.repository

import android.content.Context
import com.example.moviedb.database.MovieDatabase
import com.example.moviedb.database.MovieEntity
import com.example.moviedb.network.NetworkMonitor
import com.example.moviedb.TmdbApiService
import com.example.moviedb.MovieResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MovieRepository(
    private val context: Context,
    private val apiService: TmdbApiService,
    private val token: String
) {
    private val database = MovieDatabase.getInstance(context)
    private val dao = database.movieDao()
    private val networkMonitor = NetworkMonitor(context)
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private var currentType = "popular"

    init {
        repositoryScope.launch {
            networkMonitor.observeNetworkState().collect { isConnected ->
                if (isConnected) {
                    refreshMovies(currentType)
                }
            }
        }
    }
    fun setCurrentType(type: String) {
        currentType = type
    }

    // Just return the stream — don't auto refresh here
    fun getMovieListStream(type: String): Flow<List<MovieEntity>> {
        return dao.getMoviesByType(type).flowOn(Dispatchers.IO)
    }

    // Fetch from API and save to database
    suspend fun refreshMovies(type: String) {
        val movies = fetchMoviesFromApi(type)
        if (movies != null) {
            // Only clear and update if fetch was successful
            dao.deleteMoviesByType(type)
            val entities = movies.map { movie ->
                MovieEntity(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    posterPath = movie.poster_path ?: "",
                    type = type
                )
            }
            dao.insertAll(entities)
        }
        // If fetch failed, keep existing cache
    }

    // Clear cache for a specific type
    suspend fun clearMovies(type: String) {
        dao.deleteMoviesByType(type)
    }

    private suspend fun fetchMoviesFromApi(type: String): List<MovieResult>? {
        return try {
            val response = if (type == "popular") {
                apiService.getPopularMovies(token)
            } else {
                apiService.getTopRatedMovies(token)
            }
            response.results
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getMovieById(movieId: Int): MovieEntity? {
        return dao.getMovieById(movieId)
    }
}