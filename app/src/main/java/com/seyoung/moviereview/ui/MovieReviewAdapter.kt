package com.seyoung.moviereview.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seyoung.moviereview.R
import com.seyoung.moviereview.model.ReviewData

class MovieReviewAdapter (
    private val reviewList: List<ReviewData>
) : RecyclerView.Adapter<MovieReviewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 현재 순서 정리
        // DailyMovieChart(movieId / movieTitle / posterUrl) Intent로 전달
        // ReviewActivity 에서 Intent 값 받기
        // saveBtn 에서 ReviewData 객체 생성
        // Repository 저장 -> reviewList.add(review)
        // WriteFragment RecyclerView 출력 -> MovieReviewAdapter

        val reviewText: TextView = view.findViewById(R.id.review_title)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val reviewContent: TextView = view.findViewById(R.id.review_content)
        val reviewImg: ImageView = view.findViewById(R.id.review_img)

        fun bind(item: ReviewData) {
            reviewText.text = item.movieTitle
            ratingBar.rating = item.rating
            reviewContent.text = item.reviewText

            Glide.with(itemView.context)
                .load(item.posterUrl)
                .into(reviewImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieReviewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_review,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieReviewAdapter.ViewHolder, position: Int) {
        val item = reviewList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = reviewList.size
}