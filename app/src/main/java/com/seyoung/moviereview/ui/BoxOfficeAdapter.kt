package com.seyoung.moviereview.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.R
import com.bumptech.glide.Glide
import com.seyoung.moviereview.model.MovieItem

class BoxOfficeAdapter (
    private val items: MutableList<MovieItem> = mutableListOf()
) : RecyclerView.Adapter<BoxOfficeAdapter.ViewHolder>() {

    fun submitLis(newItems: List<MovieItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(item: MovieItem) {
            txtTitle.text = item.title
            Glide.with(itemView)
                .load(item.posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imgPoster)

            // 클릭 이벤트 처리 테스트
            itemView.setOnClickListener {
                Log.d("CLICKED", "item=$item")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_box_office,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}