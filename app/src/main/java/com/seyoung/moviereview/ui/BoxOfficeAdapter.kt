package com.seyoung.moviereview.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.R
import com.seyoung.moviereview.model.DailyBoxOffice

class BoxOfficeAdapter (
    private val list: List<DailyBoxOffice>
) : RecyclerView.Adapter<BoxOfficeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.tvRank)
        val name: TextView = view.findViewById(R.id.tvMovieName)
        val openDate: TextView = view.findViewById(R.id.tvOpenDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_box_office,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.rank.text = item.rank
        holder.name.text = item.movieNm
        holder.openDate.text = "개봉일 : ${item.openDt}"
    }

    override fun getItemCount(): Int = list.size
}