package com.seyoung.moviereview.model

// RecyclerView에 실제로 뿌릴 영화 아이템 모델 (순위 + 제목 + 포스터URL)
data class MovieItem(
    val rank: String,
    val title: String,
    val posterUrl: String?,      // 해당 변수가 null일수도 있음을 나타냄
    val movieId: Int
)