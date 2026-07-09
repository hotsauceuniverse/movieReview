package com.seyoung.moviereview.ui

import android.content.Intent
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

    // 변수 저장 후 데이터 넘기기
    private var movieTitle : String = ""
    private var reviewText : String = ""
    private var movieId: Int = -1
    private var posterUrl : String = ""
    private var imageList : ArrayList<String> = arrayListOf()
    private var rating : Float = 0f
    private var position : Int = -1     // 신규 작성인지 수정인지 구분할 값

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
        editReview()
    }

    // 작성한 리뷰를 보여주는 화면
    fun reviewDataGet() {
        // MovieReviewAdapter에서 Intent로 넘어온 데이터 받기
        // 상단 멤버 변수가 있는데 지역번수(val)을 새로 만들고 있어서 넘길때 빈 값으로 넘어감
        movieTitle = intent.getStringExtra("movieTitle") ?: ""
        reviewTitle.text = movieTitle
        Log.d("CHECK_1", "받은 movieTitle = $movieTitle")

        reviewText = intent.getStringExtra("reviewText") ?: ""
        reviewOverview.text = reviewText
        Log.d("CHECK_1", "받은 reviewText = $reviewText")

        posterUrl = intent.getStringExtra("posterUrl") ?: ""
        Glide.with(this)
            .load(posterUrl)
            .into(posterPath)

        movieId = intent.getIntExtra("movieId", -1)
        position = intent.getIntExtra("reviewPosition", -1)

//        val rating = intent.getStringExtra("rating") ?: "0"
//        ratingBar.rating = rating.toFloat()

//        val imageList = intent.getStringArrayListExtra("imageList") ?: arrayListOf()

        rating = intent.getStringExtra("rating")?.toFloatOrNull() ?: 0f
        ratingBar.rating = rating

        imageList = intent.getStringArrayListExtra("imageList") ?: arrayListOf()

        photoReviewRv = findViewById(R.id.photo_review_rv)
        photoReviewRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagePreviewAdapter = ImagePreviewAdapter(imageList)
        photoReviewRv.adapter = imagePreviewAdapter
    }

    // 작성된 리뷰를 ReviewActivity로 데이터를 같이 넘겨서 수정
    fun editReview() {
        // 수정하기 버튼 구현 필요
        editBtn = findViewById(R.id.edit_btn)
        editBtn.setOnClickListener {

            Log.d("CHECK_2", "수정버튼 movieTitle=$movieTitle")
            Log.d("CHECK_2", "수정버튼 reviewText=$reviewText")

            val intent = Intent(this, ReviewActivity::class.java)

            intent.putExtra("reviewPosition", position)

            intent.putExtra("movieId", movieId)
            Log.d("review_movieId", movieId.toString())     // movieId : 1339713

            intent.putExtra("movieTitle", movieTitle)
            Log.d("review_movieTitle", movieTitle)          // 눈동자

            intent.putExtra("reviewText", reviewText)
            Log.d("review_reviewText", reviewText)          // 11111111

            intent.putExtra("posterPath", posterUrl)
            Log.d("review_posterUrl", posterUrl)

            intent.putStringArrayListExtra("imageList", imageList)
            Log.d("review_imageList", imageList.toString())

            intent.putExtra("rating", rating)
            Log.d("review_rating", ratingBar.rating.toString())

            startActivity(intent)
            finish()
        }
    }
}