package com.seyoung.moviereview.ui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seyoung.moviereview.R
import com.seyoung.moviereview.model.Movie

class SearchAdapter (
    private val items: MutableList<Movie> = mutableListOf()
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    fun submitList(newItem: List<Movie>) {
        items.clear()
        items.addAll(newItem)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val searchPoster: ImageView = view.findViewById(R.id.Ct_imgPoster)
        private val searchTitle: TextView = view.findViewById(R.id.Ct_txtTitle)

        fun bind(item: Movie) {
            if (item.title != null) {
                searchTitle.text = item.title
            } else if (item.name != null) {
                searchTitle.text = item.name
            } else {
                searchTitle.text = "제목 없음"
            }

            val searchImgPoster = "https://image.tmdb.org/t/p/w500${item.poster_path}"

            Glide.with(itemView)
                .load(searchImgPoster)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(searchPoster)

            itemView.setOnClickListener {
                Log.d("Search_CLICKED", "item=$item")
                Log.d("Search_ID_CLICKED", "item.movieId=${item.id}")

                // 검색된 영화 클릭 시, 상세페이지로 이동 (movieId 같이 전달)
                val context = itemView.context
                val intent = Intent(context, DailyMovieChart::class.java).apply {
                    putExtra("movieId", item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_moive_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}