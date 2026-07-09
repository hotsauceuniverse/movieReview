package com.seyoung.moviereview.ui

import android.content.Intent
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

            // 리스트 아이템 클릭
            itemView.setOnClickListener {
                // EX> ReviewData(reviewText=ff, rating=4.5, writeDate=2026 - 05 - 13, imageList=[], movieId=1007757, movieTitle=뒤바뀐 친구들의 신비한 모험, posterUrl=https://image.tmdb.org/t/p/w500/uv0iRQXDbtWHo1zPk3TZNo4Biti.jpg)
                Log.d("itemViewList", item.toString())

                // ReviewPreviewActivity 화면으로 이동
                val intent = Intent(itemView.context, ReviewPreviewActivity::class.java)

                intent.putExtra("reviewPosition", bindingAdapterPosition)       // position 값
                Log.d("reviewPosition", bindingAdapterPosition.toString())

                intent.putExtra("movieTitle", item.movieTitle)                  // 영화 제목
                Log.d("movieTitle", item.movieTitle)

                intent.putExtra("rating", item.rating.toString())               // 별점
                Log.d("rating", item.rating.toString())

                intent.putExtra("writeDate", item.writeDate)                    // 작성 날짜
                Log.d("writeDate", item.writeDate)

                intent.putExtra("reviewText", item.reviewText)                  // 작성 리뷰
                Log.d("reviewText", item.reviewText)

                intent.putExtra("posterUrl", item.posterUrl)                    // 영화 포스터
                Log.d("posterUrl", item.posterUrl)

                intent.putExtra("movieId", item.movieId)                        // 영화 ID
                Log.d("movieId", item.movieId.toString())

                intent.putStringArrayListExtra("imageList", item.imageList)     // 사용자가 업로드한 사진
                Log.d("imageList", item.imageList.toString())

                itemView.context.startActivity(intent)
            }

            Log.d("POSTER_BIND", item.posterUrl)
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