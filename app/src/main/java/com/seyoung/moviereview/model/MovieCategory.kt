package com.seyoung.moviereview.model

// UI에서 쓸 영화 카테고리 모델 ("현재 상영중", "인기 영화" + apiType)
data class MovieCategory(
    val title: String,
    val apiType: String
)
