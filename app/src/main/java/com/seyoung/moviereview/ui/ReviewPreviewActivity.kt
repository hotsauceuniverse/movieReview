package com.seyoung.moviereview.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seyoung.moviereview.R

class ReviewPreviewActivity : AppCompatActivity() {

    private lateinit var infoBackBtn : ImageView
    private lateinit var editBtn : TextView
    private lateinit var posterPath : ImageView
    private lateinit var reviewTitle : TextView
    private lateinit var reviewOverview : TextView
    private lateinit var ratingBar : RatingBar

    private lateinit var photoReviewRv : RecyclerView
    private lateinit var imagePreviewAdapter: ImagePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_preview)

        infoBackBtn = findViewById(R.id.info_back_btn)
        infoBackBtn.setOnClickListener {
            finish()
        }

        posterPath = findViewById(R.id.poster_path)
        reviewTitle = findViewById(R.id.review_title)
        reviewOverview = findViewById(R.id.review_overview)
        ratingBar = findViewById(R.id.ratingBar)

        reviewDataGet()

        // 수정하기 버튼 구현 필요
        editBtn = findViewById(R.id.edit_btn)
    }

    fun reviewDataGet() {
        // Intent로 넘어온 데이터 받기
        val movieTitle = intent.getStringExtra("movieTitle") ?: ""
        reviewTitle.text = movieTitle

        val reviewText = intent.getStringExtra("reviewText") ?: ""
        reviewOverview.text = reviewText

        val posterUrl = intent.getStringExtra("posterUrl") ?: ""
        Glide.with(this)
            .load(posterUrl)
            .into(posterPath)

        val rating = intent.getStringExtra("rating") ?: ""
        ratingBar.rating = rating.toFloat()

        val imageList = intent.getStringArrayListExtra("imageList") ?: arrayListOf()
        photoReviewRv = findViewById(R.id.photo_review_rv)
        photoReviewRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagePreviewAdapter = ImagePreviewAdapter(imageList)
        photoReviewRv.adapter = imagePreviewAdapter
    }
}