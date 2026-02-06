package com.seyoung.moviereview.model

// TMDB 카테고리 API 응답 모델 (results 안에 여러 개의 TmdbMovieInfo)
data class TmdbMovieListResponse(
    val page: Int,
    val results: List<TmdbMovie>
)