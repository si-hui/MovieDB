package com.example.moviedb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviedb.ui.theme.MovieDBTheme

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?
)


val movieList = listOf(
    Movie(157336, "Interstellar", "A team of explorers travel through a wormhole in space.", "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"),
    Movie(238, "The Godfather", "The aging patriarch of a crime dynasty transfers control to his son.", "/3bhkrj58Vtu7enYsRolD1fZdja1.jpg"),
    Movie(424, "Schindler's List", "A businessman saves the lives of over a thousand Jewish refugees.", "/sF1U4EUQS8YHUYjNl3pMGNIQyr0.jpg"),
    Movie(278, "The Shawshank Redemption", "Two imprisoned men bond over years, finding solace and redemption.", "/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg"),
    Movie(372058, "Your Name", "Two strangers find they are linked in a bizarre way.", "/q719jXXEzOoYaps6babgKnONONX.jpg")
)

data class MovieDetail(
    val movieId: Int,
    val genres: List<String>,
    val homepage: String,
    val imdbId: String
)

val movieDetailList = listOf(
    MovieDetail(
        movieId = 157336,
        genres = listOf("Adventure", "Drama", "Science Fiction"),
        homepage = "https://www.interstellar.film",
        imdbId = "tt0816692"
    ),
    MovieDetail(
        movieId = 238,
        genres = listOf("Drama", "Crime"),
        homepage = "https://www.paramountmovies.com/movies/the-godfather",
        imdbId = "tt0068646"
    ),
    MovieDetail(
        movieId = 424,
        genres = listOf("Drama", "History", "War"),
        homepage = "https://www.universalpictures.com",
        imdbId = "tt0108052"
    ),
    MovieDetail(
        movieId = 278,
        genres = listOf("Drama"),
        homepage = "https://www.warnerbros.com",
        imdbId = "tt0111161"
    ),
    MovieDetail(
        movieId = 372058,
        genres = listOf("Romance", "Animation", "Drama"),
        homepage = "https://www.yourname.movie",
        imdbId = "tt5311514"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieDBTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "movieList") {
                    composable("movieList") {
                        MovieGridScreen(navController)
                    }
                    composable("movieDetail/{movieId}") { backStackEntry ->
                        val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                        val movie = movieList.find { it.id == movieId }
                        if (movie != null) {
                            MovieDetailScreen(
                                movie = movie,
                                onBack = { navController.popBackStack() },
                                onNavigateToExtras = { navController.navigate("movieExtras/${movie.id}") }
                            )
                        }
                    }
                    composable("movieExtras/{movieId}") { backStackEntry ->
                        val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                        val movie = movieList.find { it.id == movieId }
                        if (movie != null) {
                            MovieExtrasScreen(movie = movie, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun MovieListScreen(navController: NavController) { //Changed to usiing Grid
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )
    {
        item {
            Button(
                onClick = { navController.navigate("aboutScreen") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("About")
            }
        }

        items(movieList) { movie ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("movieDetail/${movie.id}") }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = movie.title, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = movie.overview)
                }
            }
        }
    }
}