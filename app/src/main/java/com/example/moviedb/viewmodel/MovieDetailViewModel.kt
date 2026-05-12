package com.example.moviedb.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.API_TOKEN
import com.example.moviedb.TmdbApi
import kotlinx.coroutines.launch

class MovieDetailViewModel : ViewModel() {

    var genres by mutableStateOf<List<String>>(emptyList())
        private set

    var homepage by mutableStateOf("")
        private set

    var imdbId by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Track if already loaded to prevent reloading on rotation
    private var loadedMovieId: Int? = null

    fun loadMovieDetail(movieId: Int) {
        // Don't reload if already loaded for this movie
        if (loadedMovieId == movieId) return

        loadedMovieId = movieId
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = TmdbApi.service.getMovieDetail(movieId, API_TOKEN)
                genres = response.genres.map { it.name }
                homepage = response.homepage ?: ""
                imdbId = response.imdb_id ?: ""
            } catch (e: Exception) {
                errorMessage = "Failed to load movie details: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}