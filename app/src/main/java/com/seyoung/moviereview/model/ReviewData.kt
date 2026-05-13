package com.seyoung.moviereview.model

data class ReviewData (
    val reviewText: String,
    val rating: Float,
    val writeDate: String,
    val imageList: ArrayList<String>,      // 사용자가 업로드한 이미지

    val movieId: Int,
    val movieTitle: String,
    val posterUrl: String               // 해당 영화 포스터 이미지
)