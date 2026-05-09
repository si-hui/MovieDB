package com.example.moviedb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviedb.database.MovieEntity
import com.example.moviedb.repository.MovieRepository
import com.example.moviedb.ui.theme.MovieDBTheme
import com.example.moviedb.viewmodel.MovieViewModel

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = MovieRepository(
            context = applicationContext,
            apiService = TmdbApi.service,
            token = API_TOKEN
        )
        val viewModel = MovieViewModel(repository)

        setContent {
            MovieDBTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "movieList") {

                    composable("movieList") {
                        MovieGridScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    composable("movieDetail/{movieId}") { backStackEntry ->
                        val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                        var movieEntity by remember { mutableStateOf<MovieEntity?>(null) }
                        LaunchedEffect(movieId) {
                            if (movieId != null) {
                                movieEntity = repository.getMovieById(movieId)
                            }
                        }
                        if (movieEntity != null) {
                            val movie = Movie(
                                id = movieEntity!!.id,
                                title = movieEntity!!.title,
                                overview = movieEntity!!.overview,
                                posterPath = movieEntity!!.posterPath
                            )
                            MovieDetailScreen(
                                movie = movie,
                                onBack = { navController.popBackStack() },
                                onNavigateToExtras = {
                                    navController.navigate("movieExtras/${movie.id}")
                                }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    composable("movieExtras/{movieId}") { backStackEntry ->
                        val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                        var movieEntity by remember { mutableStateOf<MovieEntity?>(null) }
                        LaunchedEffect(movieId) {
                            if (movieId != null) {
                                movieEntity = repository.getMovieById(movieId)
                            }
                        }
                        if (movieEntity != null) {
                            val movie = Movie(
                                id = movieEntity!!.id,
                                title = movieEntity!!.title,
                                overview = movieEntity!!.overview,
                                posterPath = movieEntity!!.posterPath
                            )
                            MovieExtrasScreen(
                                movie = movie,
                                onBack = { navController.popBackStack() }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}