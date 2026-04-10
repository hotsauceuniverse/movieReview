package com.seyoung.moviereview.model

data class MovieResponse(
    val results: List<Movie>
)

data class Movie(
    val title : String?,     // 영화 카테고리는 json에서 title으로 들어감
    val poster_path : String?,
    val name : String?,     // TV 카테고리는 json에서 name으로 들어감
    val media_type: String,
    val id: Int
)