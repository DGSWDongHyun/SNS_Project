package com.project.sns.data.module

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Menu
import android.view.SubMenu
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.sns.GlideApp
import com.project.sns.data.board.Genre
import com.project.sns.data.board.PostData
import com.project.sns.data.board.User
import com.project.sns.databinding.FragmentHomeBinding
import com.project.sns.databinding.FragmentProfileBinding
import com.project.sns.ui.activities.write.WriteActivity
import com.project.sns.ui.adapters.WriteAdapter
import com.project.sns.ui.viewmodel.MainViewModel
import kotlinx.coroutines.*

class FirebaseDatabaseModule {
    companion object {
        val database = FirebaseDatabase.getInstance().reference
        private var menu : Menu?= null
        var menuItemId = 0
        var users : User ?= null
        private var subMenu : SubMenu?= null
        val postDataList : ArrayList<PostData> = ArrayList<PostData>()
        const val NAME = 1

        fun readCategory(activity : WriteActivity, adapter : ArrayAdapter<String>){
            //read category ( genre )
            database.child("board").child("genre").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val dataObject = snapshot.getValue(Genre::class.java)
                    if (dataObject?.isVisible!!) {
                        adapter.add(dataObject.genre)
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
            activity.showDialog()
            //end
        }
         fun getUserName(nameText: TextView, context : Context) {
            val sharedPreference : SharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE)
            database.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (sharedPreference.getString("Email", null).equals(user!!.userEmail)) {
                            nameText.text = user.userName
                            users = user
                            break
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        fun findBoard(name : String, writeAdapter: WriteAdapter, code : Int){
            // read bbs db
            postDataList.clear()

            database.child("board").child("path").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val dataObject = snapshot.getValue(PostData::class.java)
                    if(name == dataObject?.UserName){
                        postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                                dataObject.file_url, dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                                dataObject.commentList, dataObject.key))
                    }

                    writeAdapter.setData(postDataList)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    postDataList.clear()
                    val dataObject = snapshot.getValue(PostData::class.java)
                    if(name == dataObject?.UserName){
                        postDataList.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                                dataObject.file_url,dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                                dataObject.commentList, dataObject.key))
                    }
                    writeAdapter.setData(postDataList)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    postDataList.clear()
                    val dataObject = snapshot.getValue(PostData::class.java)
                    if(name == dataObject?.UserName){
                        postDataList.add(0, PostData(dataObject.title, dataObject.content, dataObject.image_url,
                                dataObject.file_url,dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                                dataObject.commentList, dataObject.key))
                    }
                    writeAdapter.setData(postDataList)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Error", databaseError.message)
                }
            })

            //end
        }




        fun findBoard(title : String, writeAdapter : WriteAdapter){
            // read bbs db
            postDataList.clear()

            database.child("board").child("path").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val dataObject = snapshot.getValue(PostData::class.java)
                    if(dataObject?.genre == title){
                        postDataList.add(0, PostData(dataObject.title, dataObject.content, dataObject.image_url,
                                dataObject.file_url, dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                                dataObject.commentList, dataObject.key))
                    }
                    if(title == "전체 과목"){
                        postDataList.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                                dataObject.file_url, dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                                dataObject.commentList, dataObject.key))
                    }

                    writeAdapter.setData(postDataList)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onChildRemoved(snapshot: DataSnapshot) {
                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Error", databaseError.message)
                }
            })

            //end
        }

        fun readCategory(homeBinding : FragmentHomeBinding){
            menu = homeBinding.navView.menu
            subMenu = menu!!.addSubMenu("과목")!!
            subMenu!!.add("전체 과목")
            //read category ( genre )
            database.child("board").child("genre").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val dataObject = snapshot.getValue(Genre::class.java)
                    if (dataObject?.isVisible!!) {
                        subMenu!!.add(0, menuItemId, 0, dataObject.genre)
                        menuItemId++
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

            //end
        }

        suspend fun readBoard(writeAdapter: WriteAdapter) {
            // read bbs db
            writeAdapter.clearData()
            val jobAwait : Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {

                val jobProgress : Job = GlobalScope.async {
                    database.child("board").child("path").addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val dataObject = snapshot.getValue(PostData::class.java)
                            postDataList.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                                    dataObject.file_url, dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                                    dataObject.commentList, dataObject.key))
                            writeAdapter.setData(postDataList)
                        }

                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                        }

                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("Error", databaseError.message)
                        }
                    })
                }

                jobProgress.join()

                true
            }

            jobAwait.await()

            //end
        }
    }
}