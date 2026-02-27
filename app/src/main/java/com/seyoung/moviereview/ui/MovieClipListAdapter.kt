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
import com.seyoung.moviereview.model.MovieClipItem

class MovieClipListAdapter (
    private val items: MutableList<MovieClipItem> = mutableListOf()
) : RecyclerView.Adapter<MovieClipListAdapter.ViewHolder>() {

    fun submitList(newItems: List<MovieClipItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieClipTitle : TextView = view.findViewById(R.id.movie_clip_title)
        val movieClipItem : ImageView = view.findViewById(R.id.movie_clip_item)
        val playBtn : ImageView = view.findViewById(R.id.play_btn)

        fun bind(item: MovieClipItem) {
            Log.d("MovieClipItem", "name=${item.name}, key=${item.key}")
            movieClipTitle.text = item.name

            // key값으로 유튜브 썸네일 로드
            // https://img.youtube.com/vi/N4CZUdKRyVI/default.jpg
            val preview = "https://img.youtube.com/vi/${item.key}/default.jpg"
            Log.d("preview", preview)

            Glide.with(itemView)
                .load(preview)
                .into(movieClipItem)

            // key값으로 해당 유튜브 영상 url 연결
            // https://www.youtube.com/watch?v=N4CZUdKRyVI
            val youtubeKey = "https://www.youtube.com/watch?v=${item.key}"
            Log.d("youtubeKey", youtubeKey)

            val clickListener = View.OnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(youtubeKey))
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                itemView.context.startActivity(intent)
            }

            movieClipItem.setOnClickListener(clickListener)
            playBtn.setOnClickListener(clickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_clip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieClipListAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}

