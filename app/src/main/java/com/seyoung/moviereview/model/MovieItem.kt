package com.seyoung.moviereview.model

data class MovieItem(
    val rank: String,
    val title: String,
    val posterUrl: String?      // 해당 변수가 null일수도 있음을 나타냄
)