package com.example.moviedb

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


const val API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlZWZmODI4ZjFkYmMwMzMwOGYxODJjOTIwYTRkMmQ0NCIsIm5iZiI6MTc3NTY1MzI0Ny42OTkwMDAxLCJzdWIiOiI2OWQ2NTE3ZjkzY2ViNjkzYzBjYzA4ZjYiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.f_nHhi0Pr1HmZ9gA9Db28MY83HgDv121TesfZJ_dmCY"

@Composable
fun MovieExtrasScreen(movie: Movie, onBack: () -> Unit) {
    val viewModel: MovieExtrasViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val reviews = viewModel.reviews
    val videos = viewModel.videos
    val isLoadingReviews = viewModel.isLoadingReviews
    val isLoadingVideos = viewModel.isLoadingVideos

    LaunchedEffect(movie.id) {
        viewModel.loadExtras(movie.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Reviews Section
        Text(text = "Reviews", fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoadingReviews) {
            CircularProgressIndicator()
        } else if (reviews.isEmpty()) {
            Text(text = "No reviews found.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Videos Section
        Text(text = "Trailers", fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoadingVideos) {
            CircularProgressIndicator()
        } else if (videos.isEmpty()) {
            Text(text = "No trailers found.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(videos) { video ->
                    VideoCard(video = video)
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = review.author,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.content,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun VideoCard(video: Video) {
    val context = LocalContext.current
    val videoUrl = "https://www.youtube.com/watch?v=${video.key}"

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Card(
        modifier = Modifier
            .width(320.dp)
            .height(200.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = video.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

