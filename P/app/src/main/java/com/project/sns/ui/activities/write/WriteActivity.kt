package com.project.sns.ui.activities.write

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.sns.R
import com.project.sns.data.board.Comment
import com.project.sns.data.board.Genre
import com.project.sns.data.board.PostData
import com.project.sns.data.module.FirebaseDatabaseModule
import com.project.sns.databinding.ActivityWriteBinding
import com.project.sns.ui.viewmodel.MainViewModel
import com.tapadoo.alerter.Alert


class WriteActivity : AppCompatActivity() {

    var writeBinding : ActivityWriteBinding ?= null
    var postData : PostData?= null
    var viewModel : MainViewModel ?= null
    var database : DatabaseReference ?= null
    var commentList : HashMap<String, Comment> ?= HashMap()
    private lateinit var photoURI : Uri
    private lateinit var fileURI : Uri
    private var arrayAdapterGenre : ArrayAdapter<String>?= null
    lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(writeBinding?.root)

        initLayout()
    }

    private fun initLayout() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        database = Firebase.database.reference


        writeBinding?.selectImage?.setOnClickListener {
            selectAlbum()
        }

        writeBinding?.selectFile?.setOnClickListener {
            selectFile()
        }

        writeBinding?.selectSubject?.setOnClickListener {
            arrayAdapterGenre = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item)
            FirebaseDatabaseModule.readCategory(this, arrayAdapterGenre!!)
        }


        writeBinding?.fabWrite!!.setOnClickListener {
            if(writeBinding?.subjectTextview?.text != "과목 선택"){
                if(writeBinding!!.titleEditText.text!!.isNotEmpty() && writeBinding!!.contentEditText.text!!.isNotEmpty()) {
                    if(PHOTO_)
                        makeConfirmDialog(FROM_ALBUM)
                    else if(FILE_)
                        makeConfirmDialog(FILE)
                    else {
                        val dataLocation = database!!.child("board").child("path").push()
                        postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), "", "", System.currentTimeMillis(), writeBinding?.subjectTextview?.text.toString(), BOARD, intent.getStringExtra("userName").toString(), 0, commentList, dataLocation.key!!)
                        dataLocation.setValue(postData)
                        setResult(RESULT_OK)
                        finish()

                        overridePendingTransition(R.anim.visible, R.anim.invisible);
                    }
                }else{
                    Toast.makeText(applicationContext, "제목이나 내용 중에 누락된 부분이 있습니다.", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext, "과목을 선택해주세요.", Toast.LENGTH_LONG).show()
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
                        photoURI = data.data!!
                        if(!PHOTO_) {
                            writeBinding?.expandableLayout?.expand()
                            writeBinding?.imageAccept?.text = "${writeBinding?.imageAccept?.text} 이미지 첨부됨."
                            PHOTO_ = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            FILE -> {
                if(data?.data != null) {
                    try{
                        fileURI = data.data!!
                        if(!FILE_) {
                            writeBinding?.expandableLayout?.expand()
                            writeBinding?.imageAccept?.text = "${writeBinding?.imageAccept?.text} 파일 첨부됨."
                            FILE_ = true
                        }
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun showDialog(){
        val view : View = LayoutInflater.from(this).inflate(R.layout.dialog_selected, null)
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val button = view.findViewById<Button>(R.id.ok)

        spinner.adapter = arrayAdapterGenre

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(view)

        val alertDialogBuilt = alertDialog.create()
        alertDialogBuilt.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        button.setOnClickListener {
            alertDialogBuilt.dismiss()
            writeBinding?.subjectTextview?.text = spinner.selectedItem.toString()
        }


        alertDialogBuilt.show()
    }

    private fun makeConfirmDialog(flag: Int) {

        val filename = "uploaded" + "_" + System.currentTimeMillis()
        val storage : FirebaseStorage ?= FirebaseStorage.getInstance()
        val uploadTask: UploadTask

            if(PHOTO_ && FILE_) {
            }else{
                if (flag == FROM_ALBUM) {
                    storageRef = storage!!.reference.child("images/$filename")
                    uploadTask = storageRef.putFile(photoURI)
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setMessage("업로드중...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    uploadTask.addOnFailureListener(OnFailureListener {
                        exception ->  exception.printStackTrace()
                    }).addOnSuccessListener(OnSuccessListener<Any> { taskSnapshot ->
                        val dataLocation = database!!.child("board").child("path").push()
                        postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), "images/$filename", "", System.currentTimeMillis(), writeBinding?.subjectTextview?.text.toString(), BOARD, intent.getStringExtra("userName").toString(), 0, commentList, dataLocation.key!!)
                        dataLocation.setValue(postData)
                        setResult(RESULT_OK)
                        finish()
                        overridePendingTransition(R.anim.visible, R.anim.invisible);
                    })

                } else if (flag == FILE) {
                    storageRef = storage!!.reference.child("files/$filename")
                    uploadTask = storageRef.putFile(fileURI)
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setMessage("업로드중...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    uploadTask.addOnFailureListener(OnFailureListener {
                        exception ->  exception.printStackTrace()
                    }).addOnSuccessListener(OnSuccessListener<Any> { taskSnapshot ->
                        val dataLocation = database!!.child("board").child("path").push()
                        postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), "", "files/$filename", System.currentTimeMillis(), writeBinding?.subjectTextview?.text.toString(), BOARD, intent.getStringExtra("userName").toString(), 0, commentList, dataLocation.key!!)
                        dataLocation.setValue(postData)
                        setResult(RESULT_OK)
                        finish()
                        overridePendingTransition(R.anim.visible, R.anim.invisible);
                    })
                }
            }
    }

    private fun selectAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        startActivityForResult(intent, FROM_ALBUM)
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE)
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
        const val FILE = 4

        var FILE_ = false
        var PHOTO_ = false
    }
}