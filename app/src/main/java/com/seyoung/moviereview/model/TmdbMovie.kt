package com.seyoung.moviereview.model

// TMDB에서 내려주는 "영화 1개 정보" 모델 (카테고리 / 검색 공용)
data class TmdbMovie(
    val id: Int,
    val title: String,
    val poster_path: String?
)
