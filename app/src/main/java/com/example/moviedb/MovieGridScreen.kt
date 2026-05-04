package com.example.moviedb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviedb.viewmodel.MovieViewModel

@Composable
fun MovieGridScreen(navController: NavController, viewModel: MovieViewModel) {

    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Show loading indicator while first data is being fetched
    if (isLoading && movies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Show empty state when no movies are cached and no internet
    if (movies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No movies available.\nCheck internet and refresh.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.refresh() }) {
                    Text("Retry")
                }
            }
        }
        return
    }


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clickable { navController.navigate("movieDetail/${movie.id}") }
            ) {
                Column {
                    // --- NEW: Poster image section ---
                    val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = "Poster for ${movie.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)   // Adjust this height as needed
                    )

                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = movie.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movie.overview,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}