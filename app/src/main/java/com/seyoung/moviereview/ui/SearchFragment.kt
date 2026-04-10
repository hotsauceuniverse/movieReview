package com.seyoung.moviereview.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.BuildConfig
import com.seyoung.moviereview.R
import com.seyoung.moviereview.api.RetrofitClient
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var searchBox: EditText
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter

    val TMDB_Key = BuildConfig.TMDB_API_KEY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 검색 기능 구현 예정
        searchBox = view.findViewById(R.id.search_box)
        searchBox.setOnClickListener {
            val searchQuery = searchBox.text.toString()
            searchMovieBox(searchQuery)
    }

        searchRecyclerView = view.findViewById(R.id.search_result)
        searchRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        searchAdapter = SearchAdapter()
        searchRecyclerView.adapter = searchAdapter

}

    private fun searchMovieBox(searchQuery: String) {
        // suspend fun를 사용하기 위해 lifecycleScope 사용
        lifecycleScope.launch {
            val response = RetrofitClient.getTmdb()
                .getSearchMovie(
                    apiKey = TMDB_Key,
                    query = searchQuery
                )
            val results = response.results

            results
                .mapNotNull { it.title ?: it.name }     // title = null 제외
                .forEach {
                    Log.d("SearchFragment", "title = $it")
                }

            // UI에 데이터 반영
            searchAdapter.submitList(results)

            // 로그 확인용
            results.forEach {
                Log.d("SearchFragment", "title=${it.title ?: it.name}")
            }
        }
    }
}
