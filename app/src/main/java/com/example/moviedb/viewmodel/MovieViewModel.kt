package com.example.moviedb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.database.MovieEntity
import com.example.moviedb.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<MovieEntity>>(emptyList())
    val movies: StateFlow<List<MovieEntity>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentType = "popular"

    init {
        loadMovies(currentType)
    }

    fun loadMovies(type: String) {
        currentType = type
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMovieListStream(type).collectLatest { movieList ->
                _movies.value = movieList
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshMovies(currentType)
        }
    }
}