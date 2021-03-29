package com.project.sns.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import com.project.sns.R
import com.project.sns.ui.adapters.listener.onClickImageListener

class ImageListAdapter(val context : Context, val arrDataImage : ArrayList<StorageReference>, val listener : onClickImageListener) : RecyclerView.Adapter<ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)

        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(context).load(arrDataImage[position]).centerCrop().into(holder.image)
        holder.itemView.setOnClickListener {
            listener.onClickImage(arrDataImage[position])
        }
    }

    override fun getItemCount(): Int {
       return if(arrDataImage.size != 0) arrDataImage.size else 0
    }
}

class ImageViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val image = view.findViewById<ImageView>(R.id.getImage)
}