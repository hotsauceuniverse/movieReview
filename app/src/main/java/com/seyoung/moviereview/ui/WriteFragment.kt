package com.seyoung.moviereview.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.R
import com.seyoung.moviereview.model.ReviewRepository

class WriteFragment : Fragment() {

    private lateinit var movieReviewRv : RecyclerView
    private lateinit var adapter: MovieReviewAdapter

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 작성한 리뷰 리스트 노출
        // 작성한 리뷰 클릭 시, 해당 리뷰 레이아웃 이동
        movieReviewRv = view.findViewById(R.id.movie_review_rv)
        movieReviewRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 리스트를 넘기는 이유 : 어떤 데이터를 출력할지 알아야 하기 때문
//        movieReviewRv.adapter = MovieReviewAdapter(ReviewRepository.reviewList)
        adapter = MovieReviewAdapter(ReviewRepository.reviewList)
        movieReviewRv.adapter = adapter
    }
}

