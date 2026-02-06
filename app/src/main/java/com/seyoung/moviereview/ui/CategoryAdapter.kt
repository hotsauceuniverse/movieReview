package com.seyoung.moviereview.ui

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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)

        fun bind(item: MovieCategory) {
            txtCategory.text = item.title
            itemView.setOnClickListener {
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
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}