package com.project.sns.ui.activities.write

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.sns.R
import com.project.sns.data.comment.Comment
import com.project.sns.data.write.PostData
import com.project.sns.databinding.ActivityWriteBinding
import com.project.sns.ui.viewmodel.MainViewModel


class WriteActivity : AppCompatActivity() {

    var writeBinding : ActivityWriteBinding ?= null
    var postData : PostData ?= null
    var viewModel : MainViewModel ?= null
    var database : DatabaseReference ?= null
    var commentList : HashMap<String, Comment> ?= HashMap()
    var photoURI : Uri ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(writeBinding?.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        database = Firebase.database.reference


        writeBinding?.imageButton?.setOnClickListener {
            selectAlbum()
        }

        writeBinding?.fabWrite!!.setOnClickListener {
            if(writeBinding!!.titleEditText.text!!.isNotEmpty() && writeBinding!!.contentEditText.text!!.isNotEmpty()) {
                makeConfirmDialog(photoURI, FROM_ALBUM)
            }else{
                Toast.makeText(applicationContext, "제목이나 내용 중에 누락된 부분이 있습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            FROM_ALBUM -> {
                if (data?.data != null) {
                    try {
                        photoURI = data.data
                        writeBinding?.imageAccept?.text = "이미지 첨부됨."
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    private fun makeConfirmDialog(data: Uri?, flag: Int) {

        val filename = "uploaded" + "_" + System.currentTimeMillis()
        val storage : FirebaseStorage ?= FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage!!.reference.child("images/$filename")
        val uploadTask: UploadTask
        var file: Uri? = null
            if (flag == TAKE_PHOTO) {
            } else if (flag == FROM_ALBUM) {
                file = data
            }
        uploadTask = storageRef.putFile(file!!)
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("업로드중...")
        progressDialog.show()

        uploadTask.addOnFailureListener(OnFailureListener {
            exception ->  exception.printStackTrace()
        }).addOnSuccessListener(OnSuccessListener<Any> { taskSnapshot ->
            val dataLocation = database!!.child("board").child("path").push()
            postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), "images/$filename", System.currentTimeMillis(), "국어", BOARD, "", 0, commentList, dataLocation.key!!)
            dataLocation.setValue(postData)
            setResult(RESULT_OK)
            finish()
            overridePendingTransition(R.anim.visible, R.anim.invisible);
        })
    }

    private fun selectAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        startActivityForResult(intent, FROM_ALBUM)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.visible, R.anim.invisible);
    }
    companion object {
        const val REFRESH = 0
        const val BOARD = 1
        const val FROM_ALBUM = 2
        const val TAKE_PHOTO = 3
    }
}