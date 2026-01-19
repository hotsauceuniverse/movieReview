package com.seyoung.moviereview.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.R
import com.seyoung.moviereview.api.RetrofitClient
import com.seyoung.moviereview.model.BoxOfficeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.seyoung.moviereview.BuildConfig
import com.seyoung.moviereview.model.TmdbSearchResponse

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    val TMDB_Key = BuildConfig.TMDB_API_KEY
    val KOBIS_Key = BuildConfig.KOBIS_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadBoxOffice()

//        Log.d("API_CHECK", "TMDB=${BuildConfig.TMDB_API_KEY}")
//        Log.d("API_CHECK", "KOBIS=${BuildConfig.KOBIS_API_KEY}")

    }

    private fun loadBoxOffice() {
        RetrofitClient.api.getDailyBoxOffice(
            apiKey = KOBIS_Key,
            targetDate = "20260110"
        ).enqueue(object : Callback<BoxOfficeResponse> {

            override fun onResponse(
                call: Call<BoxOfficeResponse>,
                response: Response<BoxOfficeResponse>
            ) {
//                KOBIS 영화 목록 RecyclerView로 불러오기
//                if (response.isSuccessful) {
//                    val list = response.body()
//                        ?.boxOfficeResult
//                        ?.dailyBoxOfficeList ?: emptyList()
//
//                    recyclerView.adapter = BoxOfficeAdapter(list)

//                TMDB의 영화 목록 불러오기
                val list = response.body()
                    ?.boxOfficeResult
                    ?.dailyBoxOfficeList
                    ?: return

                for (item in list) {
                    val movieTitle = item.movieNm
                    searchTmdb(movieTitle)
                }
            }

            override fun onFailure(call: Call<BoxOfficeResponse>, t: Throwable) {
                Log.e("KOBIS", "error", t)
            }
        })
    }

    private fun searchTmdb(movieTitle: String) {
        RetrofitClient.getTmdb()
            .searchMovie(query = movieTitle)
            .enqueue(object : Callback<TmdbSearchResponse> {
                override fun onResponse(
                    call: Call<TmdbSearchResponse>,
                    response: Response<TmdbSearchResponse>
                ) {
                    val movie = response.body()
                        ?.results
                        ?.firstOrNull()
                    Log.d("TMDB_RAW", response.body().toString())

                    if (movie != null) {
                        Log.d("MATCH", "KOBIS='$movieTitle' → TMDB='${movie.title}', poster=${movie.poster_path}")
                    } else {
                        Log.d("MATCH_FAIL", "KOBIS='$movieTitle' → TMDB 결과 없음")
                    }
                }
                override fun onFailure(call: Call<TmdbSearchResponse>, t: Throwable) {
                    Log.e("TMDB", "error", t)
                }
            })
    }
}