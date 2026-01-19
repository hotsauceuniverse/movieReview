package com.seyoung.moviereview.model

data class TmdbSearchResponse(
    val results: List<TmdbMovie>
)

data class TmdbMovie(
    val title: String,
    val poster_path: String?
)