package com.seyoung.moviereview.model

data class MovieDetailItem (
    val title : String,
    val tagline : String?,
    val overview : String,
    val backdrop_path : String?,    // TMDB null 대비
    val poster_path : String?      // TMDB null 대비
)