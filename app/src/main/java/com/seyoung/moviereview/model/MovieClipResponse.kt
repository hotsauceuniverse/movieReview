package com.seyoung.moviereview.model

data class MovieClipResponse(
    val id: Int,
    val results: List<MovieClipItem>
)