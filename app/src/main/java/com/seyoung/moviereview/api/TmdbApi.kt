package com.seyoung.moviereview.api

import com.seyoung.moviereview.BuildConfig
import com.seyoung.moviereview.model.TmdbSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("search/movie")
    fun searchMovie(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("language") language: String = "ko-KR"
    ): Call<TmdbSearchResponse>
}