package com.project.sns.data.module

import android.app.Activity
import android.content.Intent
import com.project.sns.data.board.PostData
import com.project.sns.ui.activities.write.ReadActivity
import kotlinx.coroutines.*

class SimpleIntentModule {
    companion object {
        fun simplyActivity(activity: Activity, position : Int, postData : List<PostData>) {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("title", postData[position].title)
            intent.putExtra("content", postData[position].content)
            intent.putExtra("genre", postData[position].genre)
            intent.putExtra("key", postData[position].key)
            intent.putExtra("commentCount", postData[position].commentCount)
            intent.putExtra("image", postData[position].image_url)
            intent.putExtra("file", postData[position].file_url)
            intent.putExtra("userName", postData[position].UserName)

            activity.startActivity(intent)
            activity.overridePendingTransition(com.project.sns.R.anim.pull_anim, com.project.sns.R.anim.invisible);
        }
    }
}