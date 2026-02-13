package com.seyoung.moviereview.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
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
import java.time.LocalDate

class HomeFragment : Fragment() {

    private lateinit var moreBtn: Button
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

    private val fetchedItems = mutableListOf<MovieItem>()   // API로부터 받은 전체 목록(원본)
    private val visibleItem = mutableListOf<MovieItem>()    // 화면에 보여줄 목록

    private var currentCategoryType: String? = null         // 현재 선택된 TMDB 카테고리 타입(now_playing, popular 등). 더보기/추가 로딩할 때 어떤 카테고리를 불러올지 기준
    private var currentPage = 1                             // TMDB 카테고리 API에서 현재까지 불러온 페이지 번호(기본 1). 다음 페이지 요청 시 currentPage+1
    private var isLoading = false                           // 네트워크 요청 진행 중인지 여부(중복 호출 방지, 더보기 버튼 연타 방지)
    private var isLastPage = false                          // 더 이상 불러올 데이터가 없는지 여부(응답이 비었거나 끝에 도달). true면 추가 fetch 안 함

    private val FIRST_SHOW = 20                             // 최초 진입/카테고리 변경 직후 화면에 처음 보여줄 개수(일반적으로 page=1의 20개)
    private val MORE_SHOW = 10                              // “더보기” 클릭 1번당 화면에 추가로 보여줄 개수(10개씩 증가)
    private var visibleCount = 0                            // 현재 화면(recyclerView_2)에 실제로 노출 중인 아이템 개수


    // 영화 카테고리 목록
    val categories = listOf(
        MovieCategory("현재 상영중", "now_playing"),
        MovieCategory("개봉 예정", "upcoming"),
        MovieCategory("높은 평점", "top_rated"),
        MovieCategory("인기 영화", "popular")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moreBtn = view.findViewById(R.id.more_btn)

        moreBtn.setOnClickListener{
            showMoreOrFetch()
        }

        progressBar = view.findViewById(R.id.progress_bar)

        // 일별 박스오피스 순위
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        boxOfficeAdapter = BoxOfficeAdapter()
        recyclerView.adapter = boxOfficeAdapter

        // 카테고리 클릭에 따른 영화 목록
        categoryRecyclerView = view.findViewById(R.id.recyclerView_category)
        categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(categories) { category -> onCategorySelected(category) }

        categoryRecyclerView.adapter = categoryAdapter

        // 전체 영화 순위
        recyclerView_2 = view.findViewById(R.id.recyclerView_2)
        recyclerView_2.layoutManager = GridLayoutManager(requireContext(), 2)

        // 카테고리 영화 목록용 어댑터 연결
        categoryMovieListAdapter = CategoryMovieListAdapter()
        recyclerView_2.adapter = categoryMovieListAdapter
        onCategorySelected(categories[0])

        loadBoxOffice()
    }

    private fun loadBoxOffice() {
        showProgressBar(true)

        // KOBIS 일별 박스오피스 순위 기준 : 어제 날짜
        // KOBIS 연도 형식 : yyyyMMdd
        val yesterday = LocalDate.now().minusDays(1)
        val targetDay = yesterday.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))

        RetrofitClient.api.getDailyBoxOffice(
            apiKey = KOBIS_Key,
            targetDate = targetDay
        ).enqueue(object : Callback<BoxOfficeResponse> {

            override fun onResponse(
                call: Call<BoxOfficeResponse>,
                response: Response<BoxOfficeResponse>
            ) {
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
                showProgressBar(false)
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
                        "https://image.tmdb.org/t/p/w500$it"
                    }

                    // posterUrl이 null인 MovieItem 리스트에서 제외
                    if (posterUrl != null) {
                        resultList.add(
                            MovieItem(
                                rank = rank,
                                title = movieTitle,
                                posterUrl = posterUrl
                            )
                        )
                    }

                    completedCount++

                    if (completedCount == expectedCount) {
                        // 전부 끝냈을 때 딱 한번 호출
                        resultList.sortBy { it.rank.toInt() }
                        boxOfficeAdapter.submitList(resultList)

                        showProgressBar(false)
                    }
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

    // 카테고리 선택 시 : 초기화  +1 페이지 호출
    fun onCategorySelected(category: MovieCategory) {
        currentCategoryType = category.apiType

        // 초기화
        currentPage = 1
        isLastPage = false
        isLoading = false

        fetchedItems.clear()
        visibleItem.clear()
        visibleCount = 0
        categoryMovieListAdapter.submitList(emptyList())

        fetchCategoryPage(page = 1)
    }

    // 더보기 버튼 클릭 : 10개 더 보여주기 (부족하면 다음 page fetch)
    private fun showMoreOrFetch() {
        // 남은게 있으면 10개 더 노출
        if (visibleCount < fetchedItems.size) {
            val newCount = minOf(visibleCount + MORE_SHOW, fetchedItems.size)
            applyVisibleCount(newCount)
        }

        // 다 보였는데 더 가져올 수 있으면 다음 page 요청
        if (!isLoading && !isLastPage) {
            fetchCategoryPage(page = currentPage + 1)
        }
    }

    // page 호출 + 응답처리 (버퍼 누적 -> 처음엔 20개, 이후는 필요 시 노출)
    // TMDB 카테고리 로드
    private fun fetchCategoryPage(page: Int) {
        val type = currentCategoryType ?: return
        isLoading = true

        RetrofitClient.getTmdb()
            .getMoviesByCategory(type = type, apiKey = TMDB_Key, page = page)
            .enqueue(object  : Callback<TmdbMovieListResponse> {
                override fun onResponse(
                    call: Call<TmdbMovieListResponse>,
                    response: Response<TmdbMovieListResponse>
                ){
                    isLoading = false

                    val tmdbList = response.body()?.results.orEmpty()
                    if (tmdbList.isEmpty()) {
                        isLastPage = true
                        updateMoreButton()
                        return
                    }

                    currentPage = page

                    // TMDB -> MovieItem 변환해서 버퍼에 누적
                    val startRank = fetchedItems.size + 1
                    tmdbList.forEachIndexed { idx, tmdbMoive ->
                        val posterURl = tmdbMoive.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }
                        fetchedItems.add(
                            MovieItem(
                                rank = (startRank + idx).toString(),
                                title = tmdbMoive.title,
                                posterUrl = posterURl
                            )
                        )
                    }

                    // 최초 진입이면 20개 보여주기, 그 외엔 필요한 만큼만 늘리기
                    if (visibleCount == 0) {
                        applyVisibleCount(minOf(FIRST_SHOW, fetchedItems.size))
                    } else {
                        // 더보기 눌러서 fetch해온 경우 : 10개 늘려서 보여주기
                        applyVisibleCount(minOf(visibleCount + MORE_SHOW, fetchedItems.size))
                    }
                }

                override fun onFailure(call: Call<TmdbMovieListResponse>, t: Throwable) {
                    isLoading = false
                    Log.e("TMDB", "category error   ", t)
                    updateMoreButton()
                }
            })
    }

    // 화면 갱신 + 더보기 버튼 상태
    private fun applyVisibleCount(newCount: Int) {
        visibleCount = newCount

        visibleItem.clear()
        // take는 리스트의 앞부분 부터 지정한 개수만큼 요수를 추출하여 새로운 리스트를 생성하는 메서드
        visibleItem.addAll(fetchedItems.take(visibleCount))

        categoryMovieListAdapter.submitList(visibleItem.toList())
        updateMoreButton()
    }

    private fun updateMoreButton() {
        val hasMoreToShow = visibleCount < fetchedItems.size
        val canFetchMore = !isLastPage && !isLoading

        moreBtn.visibility = if (hasMoreToShow || canFetchMore) View.VISIBLE else View.GONE
        moreBtn.isEnabled = !isLoading
    }

}