package com.seyoung.moviereview.model

// TMDB 검색 API 응답 모델 (results 안에 검색된 영화 목록)
data class TmdbSearchResponse(
    val results: List<TmdbMovie>
)