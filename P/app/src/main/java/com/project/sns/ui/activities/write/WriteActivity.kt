package com.project.sns.ui.activities.write

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.R
import com.project.sns.data.user.User
import com.project.sns.data.write.PostData
import com.project.sns.databinding.ActivityWriteBinding
import com.project.sns.ui.viewmodel.MainViewModel


class WriteActivity : AppCompatActivity() {

    var writeBinding : ActivityWriteBinding ?= null
    var postData : PostData ?= null
    var viewModel : MainViewModel ?= null
    var database : DatabaseReference ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(writeBinding?.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        database = Firebase.database.reference

        writeBinding?.fabWrite!!.setOnClickListener {
            if(writeBinding!!.titleEditText.text!!.isNotEmpty() && writeBinding!!.contentEditText.text!!.isNotEmpty()){
                if(returnUserName() != null){
                    postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), null, System.currentTimeMillis(), "red", BOARD, returnUserName())
                    database!!.child("board").child("path").push().setValue(postData)
                    setResult(RESULT_OK)
                    finish()
                    overridePendingTransition(R.anim.visible, R.anim.invisible);
                }else{
                    Toast.makeText(applicationContext, "가입이 되어있지 않습니다. 가입 후에 글쓰기를 작성해주세요.", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext, "제목이나 내용 중에 누락된 부분이 있습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.visible, R.anim.invisible);
    }
    private fun returnUserName() : String? {
        var hasName : Boolean ?= false
        val sharedPreferences = getSharedPreferences("userName", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        database!!.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children) {
                    val dataUser = snapshot.getValue(User::class.java)
                    Log.d("name", "${dataUser!!.userName} : $userName")
                    Log.d("name", userName.equals(dataUser.userName).toString())
                    if (userName.equals(dataUser.userName)) {
                        hasName = true
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return if(hasName!!) { userName } else { null }
    }
    companion object {
        const val REFRESH = 0
        const val BOARD = 1
    }
}