package com.seyoung.moviereview.api

import com.seyoung.moviereview.model.BoxOfficeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// KOBIS 박스오피스 API 정의 (일별 박스오피스 목록 가져오는 인터페이스)
interface KOBISApi {

    @GET("boxoffice/searchDailyBoxOfficeList.json")
    fun getDailyBoxOffice (
        @Query("key") apiKey: String,
        @Query("targetDt") targetDate: String
    ): Call<BoxOfficeResponse>
}