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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.sns.R
import com.project.sns.data.category.Genre
import com.project.sns.data.comment.Comment
import com.project.sns.data.write.PostData
import com.project.sns.databinding.ActivityWriteBinding
import com.project.sns.ui.viewmodel.MainViewModel
import com.tapadoo.alerter.Alert


class WriteActivity : AppCompatActivity() {

    var writeBinding : ActivityWriteBinding ?= null
    var postData : PostData ?= null
    var viewModel : MainViewModel ?= null
    var database : DatabaseReference ?= null
    var commentList : HashMap<String, Comment> ?= HashMap()
    var photoURI : Uri ?= null
    private var arrayAdapterGenre : ArrayAdapter<String>?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(writeBinding?.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        database = Firebase.database.reference


        writeBinding?.imageButton?.setOnClickListener {
            selectAlbum()
        }

        writeBinding?.selectSubject?.setOnClickListener {
            arrayAdapterGenre = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item)
            readCategory()
        }

        writeBinding?.fabWrite!!.setOnClickListener {
            if(writeBinding?.subjectTextview?.text != "과목 선택"){
                if(writeBinding!!.titleEditText.text!!.isNotEmpty() && writeBinding!!.contentEditText.text!!.isNotEmpty()) {
                    makeConfirmDialog(photoURI, FROM_ALBUM)
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
                        photoURI = data.data
                        writeBinding?.imageAccept?.text = "이미지 첨부됨."
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun showDialog(){
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
    private fun readCategory(){
        //read category ( genre )
        database?.child("board")?.child("genre")?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val dataObject = snapshot.getValue(Genre::class.java)
                if (dataObject?.isVisible!!) {
                    arrayAdapterGenre?.add(dataObject.genre)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        showDialog()
        //end
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

        if(data != null) {
            uploadTask = storageRef.putFile(file!!)
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("업로드중...")
            progressDialog.show()

            uploadTask.addOnFailureListener(OnFailureListener {
                exception ->  exception.printStackTrace()
            }).addOnSuccessListener(OnSuccessListener<Any> { taskSnapshot ->
                val dataLocation = database!!.child("board").child("path").push()
                postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), "images/$filename", System.currentTimeMillis(), writeBinding?.subjectTextview?.text.toString(), BOARD, intent.getStringExtra("userName").toString(), 0, commentList, dataLocation.key!!)
                dataLocation.setValue(postData)
                setResult(RESULT_OK)
                finish()
                overridePendingTransition(R.anim.visible, R.anim.invisible);
            })
        }else{
            val dataLocation = database!!.child("board").child("path").push()
            postData = PostData(writeBinding!!.titleEditText.text.toString(), writeBinding!!.contentEditText.text.toString(), "", System.currentTimeMillis(), writeBinding?.subjectTextview?.text.toString(), BOARD, intent.getStringExtra("userName").toString(), 0, commentList, dataLocation.key!!)
            dataLocation.setValue(postData)
            setResult(RESULT_OK)
            finish()
            overridePendingTransition(R.anim.visible, R.anim.invisible)
        }

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