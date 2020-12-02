package com.project.sns.ui.activities.write

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.R
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

        writeBinding?.contentEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(writeBinding?.titleEditText!!.text!!.isNotEmpty()){
                    writeBinding?.fabWrite!!.visibility = View.VISIBLE
                }
            }
        })
        writeBinding?.fabWrite!!.setOnClickListener {
            if(writeBinding!!.titleEditText.text!!.isNotEmpty() && writeBinding!!.contentEditText.text!!.isNotEmpty()) {
                postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), null, System.currentTimeMillis(), "red", BOARD)
                database!!.child("board").child("path").push().setValue(postData)
                setResult(RESULT_OK)
                finish()
                overridePendingTransition(R.anim.visible, R.anim.invisible);
            }else{
                Toast.makeText(applicationContext, "제목이나 내용 중에 누락된 부분이 있습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.visible, R.anim.invisible);
    }
    companion object {
        const val REFRESH = 0
        const val BOARD = 1
    }
}