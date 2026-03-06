package com.seyoung.moviereview.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.seyoung.moviereview.R

class MovieStillCutExpand : AppCompatActivity(){

    private lateinit var cropImg : ImageView
    private lateinit var closeBtn : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_movie_stillcut_expand)

        cropImg = findViewById(R.id.crop_img)
        closeBtn = findViewById(R.id.close_btn)

        closeBtn.setOnClickListener {
            finish()
        }

        // MovieStillCutAdapter 에서 intent 받기
        val stillCutUrl = intent.getStringExtra("stillCutUrl")
        Log.d("Expand_stillCutUrl", stillCutUrl.toString())

        Glide.with(this)
            .load(stillCutUrl)
            .into(cropImg)
    }
}