package com.seyoung.moviereview.api

import com.seyoung.moviereview.BuildConfig
import com.seyoung.moviereview.model.MovieClipResponse
import com.seyoung.moviereview.model.MovieDetailItem
import com.seyoung.moviereview.model.MovieStillCutResponse
import com.seyoung.moviereview.model.TmdbMovieListResponse
import com.seyoung.moviereview.model.TmdbSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// TMDB API 정의
interface TmdbApi {

    // 일별 박스오피스 순위
    @GET("search/movie")
    fun searchMovie(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("language") language: String = "ko-KR"
    ): Call<TmdbSearchResponse>

    // TMDB 공식문서 API : https://developer.themoviedb.org/reference/intro/getting-started
    // 영화 카테고리 api
    @GET("movie/{type}")
    fun getMoviesByCategory(
        @Path("type") type: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ko-KR",
        @Query("page") page: Int = 1
    ): Call<TmdbMovieListResponse>

    // 영화 상세 API
    @GET("movie/{movie_id}")
    fun getMovieDetail(
        @Path("movie_id") movieId : Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("language") language: String = "ko-KR"
    ): Call<MovieDetailItem>

    // 영화 동영상(트레일러, 클립 등) 리스트
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("language") language: String = "ko-KR"
    ): Call<MovieClipResponse>

    // 영화 스틸컷 및 팬아트 리스트
    @GET("movie/{movie_id}/images")
    fun getMovieStillCut(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("include_image_language") language: String = "xx,null"
    ): Call<MovieStillCutResponse>
}