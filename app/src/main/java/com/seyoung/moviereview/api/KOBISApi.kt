package com.seyoung.moviereview.api

import com.seyoung.moviereview.model.BoxOfficeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KOBISApi {

    @GET("boxoffice/searchDailyBoxOfficeList.json")
    fun getDailyBoxOffice (
        @Query("key") apiKey: String,
        @Query("targetDt") targetDate: String
    ): Call<BoxOfficeResponse>
}