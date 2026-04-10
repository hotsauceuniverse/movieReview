package com.seyoung.moviereview.ui

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.R
import com.seyoung.moviereview.model.MovieCategory

class CategoryAdapter(
    private val items: List<MovieCategory>,
    private val onItemClick: (MovieCategory) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedCategory = 0    // 카테고리 초기 값 하이라이트

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)

        fun bind(item: MovieCategory, position: Int) {
            txtCategory.text = item.title

            // 카테고리 선택 시, 하이라이트 되게 하기
            // 선택된 position 저장
            if (position == selectedCategory) {
                txtCategory.setTextColor(Color.parseColor("#03A9F4"))
                txtCategory.setTypeface(null, Typeface.BOLD)
            } else {
                txtCategory.setTextColor(Color.parseColor("#000000"))
                txtCategory.setTypeface(null, Typeface.NORMAL)
            }

            itemView.setOnClickListener {
                selectedCategory = position
                notifyDataSetChanged()

                onItemClick(item)
                Log.d("CategoryAdapter_Click", "item=$item")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}