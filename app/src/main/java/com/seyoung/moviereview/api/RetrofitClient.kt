package com.seyoung.moviereview.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val KOBIS_BASE_URL =
        "https://kobis.or.kr/kobisopenapi/webservice/rest/"

    // TMDB 공식 API : https://api.themoviedb.org/{version}/{endpoint}
    // 실제 요청 값 : https://api.themoviedb.org/3/search/movie
    private const val TMDB_BASE_URL =
        "https://api.themoviedb.org/3/"

    val api: KOBISApi by lazy {
        Retrofit.Builder()
            .baseUrl(KOBIS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KOBISApi::class.java)
    }

    fun getTmdb(): TmdbApi {
        return Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}