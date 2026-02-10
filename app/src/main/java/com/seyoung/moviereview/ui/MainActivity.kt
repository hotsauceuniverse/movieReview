package com.seyoung.moviereview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.seyoung.moviereview.R

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_nav)

        // 초기 Fragment 설정 (HomeFragment)
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Bottom Navigation 클릭 이벤트
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.item_2 -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.item_3 -> {
                    replaceFragment(WriteFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}