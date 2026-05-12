package com.example.moviedb

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MovieExtrasViewModel : ViewModel() {

    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set

    var videos by mutableStateOf<List<Video>>(emptyList())
        private set

    var isLoadingReviews by mutableStateOf(true)
        private set

    var isLoadingVideos by mutableStateOf(true)
        private set

    private var loadedMovieId: Int? = null

    fun loadExtras(movieId: Int) {
        if (loadedMovieId == movieId) return
        loadedMovieId = movieId
        isLoadingReviews = true
        isLoadingVideos = true

        viewModelScope.launch {
            try {
                val reviewResponse = TmdbApi.service.getMovieReviews(movieId, API_TOKEN)
                reviews = reviewResponse.results
            } catch (e: Exception) {
                reviews = emptyList()
            } finally {
                isLoadingReviews = false
            }
        }

        viewModelScope.launch {
            try {
                val videoResponse = TmdbApi.service.getMovieVideos(movieId, API_TOKEN)
                videos = videoResponse.results.filter { it.site == "YouTube" }
            } catch (e: Exception) {
                videos = emptyList()
            } finally {
                isLoadingVideos = false
            }
        }
    }
}