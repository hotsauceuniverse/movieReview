package com.seyoung.moviereview.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seyoung.moviereview.R
import com.seyoung.moviereview.model.MovieStillCutItem

class MovieStillCutAdapter (
    private val items: MutableList<MovieStillCutItem> = mutableListOf()
) : RecyclerView.Adapter<MovieStillCutAdapter.ViewHolder>() {

    fun submitList(newItems: List<MovieStillCutItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieStillCut : ImageView = view.findViewById(R.id.movie_stillcut)

        fun bind(item: MovieStillCutItem) {

            val url = item.file_path?.let { "https://image.tmdb.org/t/p/w500$it" }
            Log.d("StillCut_Item", "filePath=${item.file_path}")

            if (url == null) return

            Glide.with(itemView)
                .load(url)
                .into(movieStillCut)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_stillcut, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieStillCutAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}