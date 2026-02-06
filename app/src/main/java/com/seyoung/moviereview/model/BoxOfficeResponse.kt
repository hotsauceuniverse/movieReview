package com.seyoung.moviereview.model

// KOBIS API 응답 모델
data class BoxOfficeResponse(
    val boxOfficeResult: BoxOfficeResult
)

data class BoxOfficeResult(
    val dailyBoxOfficeList: List<DailyBoxOffice>
)

data class DailyBoxOffice(
    val rank: String,
    val movieNm: String,
    val openDt: String
)