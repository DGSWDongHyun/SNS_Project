package com.project.sns.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.sns.GlideApp
import com.project.sns.R
import com.project.sns.data.comment.Comment
import com.project.sns.data.write.PostData
import com.project.sns.ui.adapters.listener.onClickItemListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class CommentAdapter(private val aContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var positionCheck = 0
    private var isStartViewCheck = true
    var database : DatabaseReference?= null
    private var commentData: List<Comment>? = null

    fun setData(commentData : ArrayList<Comment>?) {
        this.commentData = commentData
        notifyDataSetChanged()
    }

    fun getData() : List<Comment>{
        return commentData!!;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var root : View?= null
        root = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(root!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (commentContent, commentDateTime, user) = commentData!![position]

        if(holder is CommentViewHolder){

            val storage : FirebaseStorage?= FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage!!.reference.child("${user?.userProfile}")
            GlideApp.with(holder.itemView).load(storageRef).into(holder.profile)

            holder.title.text = user?.userName
            holder.commentContent.text = commentContent
            holder.dateTime.text = SimpleDateFormat("yyyy.MM.dd HH : mm").format(commentDateTime).toString()


            if (isStartViewCheck) {
                if (position > 6) isStartViewCheck = false
            } else {
                if (position > positionCheck) {
                    holder.view_animation.animation = AnimationUtils.loadAnimation(aContext, R.anim.fall_down)
                } else {
                    holder.view_animation.animation = AnimationUtils.loadAnimation(aContext, R.anim.raise_up)
                }
            }
            positionCheck = position
        }

    }

    override fun getItemCount(): Int {
        return if (commentData != null) commentData!!.size else 0
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile : CircleImageView = itemView.findViewById(R.id.profile_image)
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val dateTime : TextView = itemView.findViewById(R.id.textDateTime)
        val view_animation: ConstraintLayout = itemView.findViewById(R.id.view_animation)
        val commentContent : TextView = itemView.findViewById(R.id.textContent)

    }

}