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
import com.seyoung.moviereview.model.MovieItem

class CategoryMovieListAdapter(
    private val items: MutableList<MovieItem> = mutableListOf()
) : RecyclerView.Adapter<CategoryMovieListAdapter.ViewHolder>() {

    // 외부에서 새 리스트 넣을 때 호출
    fun submitList(newItem: List<MovieItem>) {
        items.clear()
        items.addAll(newItem)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPoster: ImageView = view.findViewById(R.id.Ct_imgPoster)
        private val txtTitle: TextView = view.findViewById(R.id.Ct_txtTitle)

        fun bind(item: MovieItem) {
            txtTitle.text = item.title

            Glide.with(itemView)
                .load(item.posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imgPoster)

            // 영화 TOP 차트 클릭 이벤트 처리
            itemView.setOnClickListener{
                Log.d("CategoryMovieListAdapter_Click", "item=$item")
                Log.d("CategoryMovieList_ID", "item.movieId=${item.movieId}")

//                val context = itemView.context
//                val intent = Intent(context, MovieTopChart::class.java)
//                context.startActivity(intent)

                val context = itemView.context
                val intent = Intent(context, DailyMovieChart::class.java).apply {
                    putExtra("movieId", item.movieId)
                }
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_moive_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryMovieListAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}