package com.example.moviedb.repository

import android.content.Context
import com.example.moviedb.database.MovieDatabase
import com.example.moviedb.database.MovieEntity
import com.example.moviedb.network.NetworkMonitor
import com.example.moviedb.TmdbApiService
import com.example.moviedb.ApiMovie
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

    // Expose cached movies as Flow (UI will observe this)
    fun getMovieListStream(type: String): Flow<List<MovieEntity>> {
        // Whenever internet becomes available, refresh the cache for the current type
        repositoryScope.launch {
            networkMonitor.observeNetworkState().collect { isConnected ->
                if (isConnected) {
                    refreshMovies(type)
                }
            }
        }
        return dao.getMoviesByType(type).flowOn(Dispatchers.IO)
    }

    // Called manually or automatically when internet returns
    suspend fun refreshMovies(type: String) {
        // Delete only movies of this type (so only the selected view type remains)
        dao.deleteMoviesByType(type)

        // Fetch fresh data from API
        val movies = fetchMoviesFromApi(type)

        if (movies != null) {
            // Convert ApiMovie (from API) to MovieEntity (database)
            val entities = movies.map { apiMovie ->
                MovieEntity(
                    id = apiMovie.id,
                    title = apiMovie.title,
                    overview = apiMovie.overview,
                    posterPath = apiMovie.poster_path ?: "",
                    type = type
                )
            }
            dao.insertAll(entities)
        }
    }

    private suspend fun fetchMoviesFromApi(type: String): List<ApiMovie>? {
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