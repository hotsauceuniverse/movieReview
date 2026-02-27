package com.seyoung.moviereview.ui

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seyoung.moviereview.R
import com.seyoung.moviereview.api.RetrofitClient
import com.seyoung.moviereview.model.MovieClipResponse
import com.seyoung.moviereview.model.MovieDetailItem
import com.seyoung.moviereview.model.MovieStillCutItem
import com.seyoung.moviereview.model.MovieStillCutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyMovieChart : AppCompatActivity() {

    private lateinit var backdropImage : ImageView
    private lateinit var posterImage : ImageView
    private lateinit var titleText : TextView
    private lateinit var taglineText : TextView
    private lateinit var overviewText : TextView
    private lateinit var backBtn : ImageView

    // 트레일러, 스틸컷 recyclerview
    private lateinit var movieClipRv : RecyclerView
    private lateinit var movieThumbnailRv : RecyclerView

    private lateinit var movieClipAdapter: MovieClipListAdapter
    private lateinit var movieStillCutAdapter: MovieStillCutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daily_movie_chart_activity)

        backdropImage = findViewById(R.id.backdrop_path)
        posterImage = findViewById(R.id.poster_path)
        titleText = findViewById(R.id.movie_title)
        taglineText = findViewById(R.id.movie_tagline)
        overviewText = findViewById(R.id.movie_overview)
        backBtn = findViewById(R.id.info_back_btn)

        movieClipRv = findViewById(R.id.movie_clip_rv)
        movieClipRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        movieClipAdapter = MovieClipListAdapter()
        movieClipRv.adapter = movieClipAdapter

        movieThumbnailRv = findViewById(R.id.movie_thumbnail_rv)
        movieThumbnailRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        movieStillCutAdapter = MovieStillCutAdapter()
        movieThumbnailRv.adapter = movieStillCutAdapter

        // 뒤로가기 버튼
        backBtn.setOnClickListener {
            finish()
        }

        // intent에서 movie_id 받기
        val movieId = intent.getIntExtra("movieId", -1)
        Log.d("Movie_ID", movieId.toString())

        if (movieId == -1) {
            // movieId 없으면 그냥 종료
            finish()
            return
        }

        // TMDB 상세 정보 로드
        loadMovieDetail(movieId)
        loadMovieClip(movieId)
        loadMovieStillCut(movieId)
    }

    private fun loadMovieDetail(movieId: Int) {
        RetrofitClient.getTmdb()
            .getMovieDetail(movieId)
            .enqueue(object : Callback<MovieDetailItem> {

                override fun onResponse(
                    call : Call<MovieDetailItem>,
                    response : Response<MovieDetailItem>
                ) {
                    val body = response.body() ?: return

                    // title, tagline, overview 세팅
                    titleText.text = body.title

                    // tagline가 없으면 보이지 않게
                    if (body.tagline.isNullOrBlank()) {
                        taglineText.visibility = View.GONE
                    } else {
                        taglineText.text = body.tagline
                        taglineText.visibility = View.VISIBLE
                    }
                    overviewText.text = body.overview

                    // 이미지 url 만들기
                    val backdropUrl = body.backdrop_path?.let {
                        "https://image.tmdb.org/t/p/w780$it"
                    }

                    val posterUrl = body.poster_path?.let {
                        "https://image.tmdb.org/t/p/w500$it"
                    }

                    // 이미지 로드
                    Glide.with(this@DailyMovieChart)
                        .load(backdropUrl)
                        .into(backdropImage)

                    // backdrop_path Image 블러처리
                    val blurEffect = RenderEffect.createBlurEffect(
                        35f, 35f, Shader.TileMode.CLAMP
                    )
                    backdropImage.setRenderEffect(blurEffect)

                    Glide.with(this@DailyMovieChart)
                        .load(posterUrl)
                        .into(posterImage)
                }
                override fun onFailure(call: Call<MovieDetailItem>, t: Throwable) {
                    Log.d("Movie_Detail_Error", t.message.toString())
                }
            })
    }

    private fun loadMovieClip(movieId: Int) {
        RetrofitClient.getTmdb()
            .getMovieVideos(movieId)
            .enqueue(object : Callback<MovieClipResponse> {

                override fun onResponse(
                    call: Call<MovieClipResponse>,
                    response: Response<MovieClipResponse>
                ) {
                    val body = response.body() ?: return

                    val clip = body.results

                    movieClipAdapter.submitList(clip)
                }

                override fun onFailure(call: Call<MovieClipResponse>, t: Throwable) {
                    Log.d("Movie_Clip_Error", t.message.toString())
                }
            })
    }

    private fun loadMovieStillCut(movieId: Int) {
        RetrofitClient.getTmdb()
            .getMovieStillCut(movieId)
            .enqueue(object : Callback<MovieStillCutResponse> {

                override fun onResponse(
                    call: Call<MovieStillCutResponse>,
                    response: Response<MovieStillCutResponse>
                ) {
                    val body = response.body() ?: return
                    val stillCut : List<MovieStillCutItem> = body.backdrops
                    Log.d("StillCut", stillCut.size.toString())

                    movieStillCutAdapter.submitList(stillCut)
                }

                override fun onFailure(call: Call<MovieStillCutResponse>, t: Throwable) {
                    Log.d("Movie_StillCut_Error", t.message.toString())
                }
            })
    }

}