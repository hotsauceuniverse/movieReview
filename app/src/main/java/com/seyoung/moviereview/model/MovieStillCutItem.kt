package com.seyoung.moviereview.model

// TMDB /images 응답 전체
data class MovieStillCutResponse(
    val id: Int,
    val backdrops: List<MovieStillCutItem>
)

// 개별 이미지 1장
data class MovieStillCutItem(
    val file_path: String
)