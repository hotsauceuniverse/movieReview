package com.seyoung.moviereview.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seyoung.moviereview.R

class ImageAdapter(
    private val imageList: ArrayList<String>,
    private val listener: OnAddClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {       // ViewHolder 1개 Adapter<MyViewHolder>, ViewHolder 여러 개 Adapter<RecyclerView.ViewHolder>

    // 업로드 된 사진 옆 사진 추가 버튼 보이게 ImageView를 RecyclerView의 아이템으로 추가
    private val TYPE_IMAGE = 0
    private val TYPE_ADD = 1

    // 사진 최대 개수 설정
    private val MAX_COUNT = 3

    interface OnAddClickListener {
        fun onAddPhotoClick()
    }

    // NullPointerException: findViewById(...) must not be null -> View TYPE_ADD일 때는 item_add_image.xml을 쓰는데 그 레이아웃에는 보통 item_image, delete_btn 없음
    // ViewHolder 분리
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val imageView: ImageView = view.findViewById(R.id.item_image)
//        val deleteBtn: ImageView = view.findViewById(R.id.delete_btn)
//    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val deleteBtn: ImageView = view.findViewById(R.id.delete_btn)
    }

    inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            ImageViewHolder(view)
        } else {
            val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_add_image, parent, false)
            AddViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return if(imageList.size < MAX_COUNT) {
            imageList.size + 1  // add 버튼 포함
        } else {
            imageList.size      // add 버튼 숨김
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is AddViewHolder) {
            holder.itemView.setOnClickListener{
                listener.onAddPhotoClick()  // ReviewActivity로 콜백
            }
        } else if (holder is ImageViewHolder) {
            val item = imageList[position]

//            when (item) {
//                is Bitmap -> holder.imageView.setImageBitmap(item)
//                is Uri -> holder.imageView.setImageURI(item)
//            }

            Glide.with(holder.itemView.context)
                .load(item)
                .into(holder.imageView)

            holder.deleteBtn.setOnClickListener {
                val position = holder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    val wasFull = imageList.size == MAX_COUNT

                    imageList.removeAt(position)
                    notifyItemRemoved(position)

                    // 이미지 3개 채우고 delete 되면 add 버튼 생성
                    if (wasFull) {
                        notifyItemRangeChanged(position, imageList.size)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (imageList.size < MAX_COUNT && position == imageList.size) {
            TYPE_ADD
        } else {
            TYPE_IMAGE
        }
    }
}