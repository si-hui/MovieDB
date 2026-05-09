package com.example.moviedb

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviedb.viewmodel.MovieDetailViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun MovieDetailScreen(movie: Movie, onBack: () -> Unit, onNavigateToExtras: () -> Unit) {
    val context = LocalContext.current
    val viewModel: MovieDetailViewModel = viewModel()

    // Fetch details from API when screen opens
    LaunchedEffect(movie.id) {
        viewModel.loadMovieDetail(movie.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNavigateToExtras) {
            Text("Reviews & Trailers")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = movie.overview)

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isLoading) {
            CircularProgressIndicator()
        } else if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage!!,
                color = Color.Red
            )
        } else {
            // Genre composable
            if (viewModel.genres.isNotEmpty()) {
                MovieGenres(genres = viewModel.genres)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Homepage composable
            if (viewModel.homepage.isNotEmpty()) {
                MovieHomepage(homepage = viewModel.homepage, context = context)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // IMDB composable
            if (viewModel.imdbId.isNotEmpty()) {
                MovieImdbLink(imdbId = viewModel.imdbId, context = context)
            }
        }
    }
}

@Composable
fun MovieGenres(genres: List<String>) {
    Column {
        Text(text = "Genres:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = genres.joinToString(", "))
    }
}

@Composable
fun MovieHomepage(homepage: String, context: android.content.Context) {
    Column {
        Text(text = "Homepage:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = homepage,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(homepage))
            context.startActivity(intent)
        }) {
            Text("Open in Browser")
        }
    }
}

@Composable
fun MovieImdbLink(imdbId: String, context: android.content.Context) {
    val imdbUrl = "https://www.imdb.com/title/$imdbId"
    Column {
        Text(text = "IMDB:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = imdbUrl,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imdbUrl))
            context.startActivity(intent)
        }) {
            Text("Open in IMDB App")
        }
    }
}