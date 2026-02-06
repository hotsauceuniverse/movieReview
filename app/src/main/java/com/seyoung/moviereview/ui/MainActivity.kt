package com.seyoung.moviereview.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.R
import com.seyoung.moviereview.api.RetrofitClient
import com.seyoung.moviereview.model.BoxOfficeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.seyoung.moviereview.BuildConfig
import com.seyoung.moviereview.model.MovieCategory
import com.seyoung.moviereview.model.MovieItem
import com.seyoung.moviereview.model.TmdbMovieListResponse
import com.seyoung.moviereview.model.TmdbSearchResponse

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView     // 일별 박스오피스 순위
    private lateinit var recyclerView_2: RecyclerView   // 카테고리 영화
    private lateinit var categoryRecyclerView: RecyclerView

    private lateinit var boxOfficeAdapter: BoxOfficeAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryMovieListAdapter: CategoryMovieListAdapter

    private val resultList = mutableListOf<MovieItem>()
    private var expectedCount = 0       // TMDB 요청개수: 박스오피스 10위 → expectedCount = 10
    private var completedCount = 0      // TMDB 응답 완료 개수: TMDB 요청 하나가 성공하든, 실패하든 끝날 때마다 +1

    val TMDB_Key = BuildConfig.TMDB_API_KEY
    val KOBIS_Key = BuildConfig.KOBIS_API_KEY

    private lateinit var progressBar: ProgressBar

    // 영화 카테고리 목록
    val categories = listOf(
        MovieCategory("현재 상영중", "now_playing"),
        MovieCategory("개봉 예정", "upcoming"),
        MovieCategory("높은 평점", "top_rated"),
        MovieCategory("인기 영화", "popular")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_bar)

        // 일별 박스오피스 순위
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        boxOfficeAdapter = BoxOfficeAdapter()
        recyclerView.adapter = boxOfficeAdapter

        // 카테고리 클릭에 따른 영화 목록
        categoryRecyclerView = findViewById(R.id.recyclerView_category)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(categories) { category -> onCategorySelected(category) }

        categoryRecyclerView.adapter = categoryAdapter
        onCategorySelected(categories[0])

        // 전체 영화 순위
        recyclerView_2 = findViewById(R.id.recyclerView_2)
        recyclerView_2.layoutManager = GridLayoutManager(this, 2)

        // 카테고리 영화 목록용 어댑터 연결
        categoryMovieListAdapter = CategoryMovieListAdapter()
        recyclerView_2.adapter = categoryMovieListAdapter

        loadBoxOffice()

        // Key값 들어오는지 확인
//        Log.d("API_CHECK", "TMDB=${BuildConfig.TMDB_API_KEY}")
//        Log.d("API_CHECK", "KOBIS=${BuildConfig.KOBIS_API_KEY}")
    }

    private fun loadBoxOffice() {
        showProgressBar(true)

        RetrofitClient.api.getDailyBoxOffice(
            apiKey = KOBIS_Key,
            targetDate = "20260110"
        ).enqueue(object : Callback<BoxOfficeResponse> {

            override fun onResponse(
                call: Call<BoxOfficeResponse>,
                response: Response<BoxOfficeResponse>
            ) {
                // KOBIS 영화 목록 RecyclerView로 불러오기
//                if (response.isSuccessful) {
//                    val list = response.body()
//                        ?.boxOfficeResult
//                        ?.dailyBoxOfficeList ?: emptyList()
//                    recyclerView.adapter = BoxOfficeAdapter(list)

                // TMDB의 영화 목록 불러오기
                val list = response.body()
                    ?.boxOfficeResult
                    ?.dailyBoxOfficeList
                    ?: return

                val top10List = list.filter { it.rank.toInt() <= 10}

                expectedCount = top10List.size
                completedCount = 0
                resultList.clear()

                // KOBIS 박스오피스 목록에 있는 영화 개수만큼 TMDB 검색 요청을 전부 한번에 날린다
                for (item in top10List) {
                    val rank = item.rank
                    val movieTitle = item.movieNm

                    searchTmdb(rank, movieTitle)
                }
            }

            override fun onFailure(call: Call<BoxOfficeResponse>, t: Throwable) {
                Log.e("KOBIS", "error", t)
            }
        })
    }

    private fun searchTmdb(rank: String, movieTitle: String) {
        RetrofitClient.getTmdb()
            .searchMovie(
                apiKey = TMDB_Key,
                query = movieTitle
            )
            .enqueue(object : Callback<TmdbSearchResponse> {

                override fun onResponse(
                    call: Call<TmdbSearchResponse>,
                    response: Response<TmdbSearchResponse>
                ) {
                    val movie = response.body()
                        ?.results
                        ?.firstOrNull()

                    Log.d("TMDB_RAW", response.body().toString())

                    val posterUrl = movie?.poster_path?.let {
                        // TMDB 공식 문서
                        // https://ileolami.mintlify.app/parameters/basic?utm_source=chatgpt.com
                        "https://image.tmdb.org/t/p/w500$it"
                    }

                    resultList.add(
                        MovieItem(
                            rank = rank,
                            title = movieTitle,
                            posterUrl = posterUrl
                        )
                    )

                    completedCount++

                    if (completedCount == expectedCount) {
                        // 전부 끝냈을 때 딱 한번 호출
                        resultList.sortBy { it.rank.toInt() }   // sortBy : 어떤 요소로 정렬할지 선택
                        boxOfficeAdapter.submitList(resultList)

                        showProgressBar(false)
                    }

//                    if (movie != null) {
//                        Log.d("MATCH", "KOBIS='$movieTitle' → TMDB='${movie.title}', poster=${movie.poster_path}")
//                    } else {
//                        Log.d("MATCH_FAIL", "KOBIS='$movieTitle' → TMDB 결과 없음")
//                    }
                }
                override fun onFailure(call: Call<TmdbSearchResponse>, t: Throwable) {
                    completedCount++

                    if (completedCount == expectedCount) {
                        resultList.sortBy { it.rank.toInt() }
                        boxOfficeAdapter.submitList(resultList)
                        showProgressBar(false)
                    }

                    Log.e("TMDB", "error", t)
                }
            })
    }

    fun showProgressBar(show : Boolean) {
        if (show) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE
    }

    // 카테고리 선택 시
    fun onCategorySelected(category: MovieCategory) {
        loadTmdbCategory(category.apiType)
    }

    // TMDB 카테고리 로드
    private fun loadTmdbCategory(type: String) {
        RetrofitClient.getTmdb().getMoviesByCategory(type, TMDB_Key)
            .enqueue(object : Callback<TmdbMovieListResponse> {

                override fun onResponse(
                    call: Call<TmdbMovieListResponse>,
                    response: Response<TmdbMovieListResponse>
                ) {
                    val tmdbList = response.body()?.results ?: return

                    val movieItemList = mutableListOf<MovieItem>()
                    var index = 0

                    for (tmdbMovie in tmdbList) {
                        val poseterUrl: String?
                        if (tmdbMovie.poster_path != null) {
                            poseterUrl = "https://image.tmdb.org/t/p/w500" + tmdbMovie.poster_path
                        } else {
                            poseterUrl = null
                        }
                        val moiveItem = MovieItem(
                            rank = (index + 1).toString(),
                            title = tmdbMovie.title,
                            posterUrl = poseterUrl
                        )
                        movieItemList.add(moiveItem)
                        index++
                    }
                    categoryMovieListAdapter.submitList(movieItemList)
                }

                override fun onFailure(call: Call<TmdbMovieListResponse>, t: Throwable) {
                    Log.e("TMDB", "error", t)
                }
            })
    }
}