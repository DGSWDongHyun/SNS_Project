package com.project.sns.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.sns.R
import com.project.sns.data.write.PostData
import com.project.sns.ui.adapters.WriteAdapter.WriteViewHolder
import com.project.sns.ui.adapters.listener.onClickItemListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WriteAdapter(private val aContext: Context, private val listener: onClickItemListener) : RecyclerView.Adapter<WriteViewHolder>() {
    private var positionCheck = 0
    private var isStartViewCheck = true
    private var postData: List<PostData>? = null

    fun setData(postData: ArrayList<PostData>?) {
        this.postData = postData
        notifyDataSetChanged()
    }

    fun getData() : List<PostData>{
        return postData!!;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_subjects, parent, false)
        return WriteViewHolder(root)
    }

    override fun onBindViewHolder(holder: WriteViewHolder, position: Int) {
        val (title, content, image_url, dateTime) = postData!![position]

        holder.title.text = title
        holder.content?.text = content
        holder.dateTime.text = SimpleDateFormat("yyyy.MM.dd HH : mm").format(dateTime).toString()

        if (isStartViewCheck) {
            if (position > 6) isStartViewCheck = false
        } else {
            if (position > positionCheck) {
                holder.view_animation.animation = AnimationUtils.loadAnimation(aContext, R.anim.fall_down)
            } else {
                holder.view_animation.animation = AnimationUtils.loadAnimation(aContext, R.anim.raise_up)
            }
        }

        if (image_url != null) {
        }
        holder.itemView.setOnClickListener { v: View? -> listener.onClickItem(position) }
        positionCheck = position
    }

    override fun getItemCount(): Int {
        return if (postData != null) postData!!.size else 0
    }

    inner class WriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val content: TextView? = itemView.findViewById(R.id.content)
        val imageView_uploaded: ImageView = itemView.findViewById(R.id.uploaded_image)
        val dateTime : TextView = itemView.findViewById(R.id.date_time)
        val view_animation: ConstraintLayout = itemView.findViewById(R.id.view_animation)

    }
}