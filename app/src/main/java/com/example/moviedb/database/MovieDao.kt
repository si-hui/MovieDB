package com.example.moviedb.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE type = :type")
    fun getMoviesByType(type: String): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies WHERE type = :type")
    suspend fun deleteMoviesByType(type: String)

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?
}