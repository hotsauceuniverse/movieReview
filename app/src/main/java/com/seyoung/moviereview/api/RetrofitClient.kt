package com.seyoung.moviereview.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Retrofit 객체 생성 담담 (KOBIS용, TMDB용 baseUrl 세팅하고 API 인터페이스를 만들어주는 역할)
object RetrofitClient {
    private const val KOBIS_BASE_URL =
        "https://kobis.or.kr/kobisopenapi/webservice/rest/"

    // TMDB 공식 API : https://api.themoviedb.org/{version}/{endpoint}
    // 실제 요청 값 : https://api.themoviedb.org/3/search/movie
    private const val TMDB_BASE_URL =
        "https://api.themoviedb.org/3/"

    // java.net.sockettimeoutexception timeout 나서 서버 통신 connectTimeout을 늘리기
    val client = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .build()

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
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}