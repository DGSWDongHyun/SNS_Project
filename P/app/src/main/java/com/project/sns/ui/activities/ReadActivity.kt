package com.project.sns.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.sns.GlideApp
import com.project.sns.R
import com.project.sns.data.comment.Comment
import com.project.sns.data.write.PostData
import com.project.sns.databinding.ActivityReadBinding
import com.project.sns.ui.adapters.CommentAdapter

class ReadActivity : AppCompatActivity() {
    var readBinding : ActivityReadBinding ?= null
    var commentList : ArrayList<Comment> = arrayListOf()
    private var commentAdapter : CommentAdapter ?= null

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readBinding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(readBinding?.root)

        commentAdapter = CommentAdapter(applicationContext)
        database = Firebase.database.reference
        val intent = intent

        readBinding?.postTitle?.text = intent.getStringExtra("title")
        readBinding?.contentTextView?.text = intent.getStringExtra("content")
        readBinding?.detailTextView?.text = "${intent.getStringExtra("genre")} - ${intent.getStringExtra("userName")}"

        var commentCount = intent.getIntExtra("commentCount", 0)
        readComment()

        if(!intent.getStringExtra("image").isNullOrEmpty()){
            Log.d("hasExtra", intent.getStringExtra("image")!!)
            val storage : FirebaseStorage?= FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage!!.reference.child("${intent.getStringExtra("image")}")

            storageRef.downloadUrl.addOnCompleteListener {
                if(it.isSuccessful){
                    GlideApp.with(this)
                        .load (storageRef)
                        .into (readBinding?.loadedImage!!)

                }else{
                    Toast.makeText(applicationContext, "이미지 로딩에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }

        readBinding?.commentRecycler?.layoutManager = LinearLayoutManager(applicationContext)
        readBinding?.commentRecycler?.adapter = commentAdapter
        commentAdapter?.setData(commentList)

        readBinding?.sendComment?.setOnClickListener {
            database.child("board").child("path")
                    .child(intent.getStringExtra("key").toString()).child("commentList").push()
                    .setValue(Comment("name", readBinding?.commentEditText?.text.toString(), System.currentTimeMillis()))

            commentCount ++
            database.child("board").child("path")
                    .child(intent.getStringExtra("key").toString()).child("commentCount")
                    .setValue(commentCount)

            readBinding?.commentEditText?.setText("")

        }
    }
    private fun readComment(){
        database.child("board").child("path").child(intent.getStringExtra("key").toString()).child("commentList").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val dataObject = snapshot.getValue(Comment::class.java)
                if(dataObject != null){
                    commentList.add(0, Comment(dataObject.commentName, dataObject.commentContent, dataObject.commentDateTime))
                    commentAdapter?.setData(commentList)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                commentList.clear()
                val dataObject = snapshot.getValue(Comment::class.java)
                commentList.add(0, Comment(dataObject!!.commentName, dataObject.commentContent, dataObject.commentDateTime))
                commentAdapter?.setData(commentList)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                commentList.clear()
                val dataObject = snapshot.getValue(Comment::class.java)
                commentList.add(0, Comment(dataObject!!.commentName, dataObject.commentContent, dataObject.commentDateTime))
                commentAdapter?.setData(commentList)

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", databaseError.message)
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(com.project.sns.R.anim.pull_anim, com.project.sns.R.anim.push_anim);
    }
}