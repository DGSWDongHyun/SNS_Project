package com.project.sns.ui.fragments.home

import android.R
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.data.category.Genre
import com.project.sns.data.user.User
import com.project.sns.data.write.PostData
import com.project.sns.databinding.FragmentHomeBinding
import com.project.sns.ui.activities.MainActivity
import com.project.sns.ui.activities.write.WriteActivity
import com.project.sns.ui.adapters.WriteAdapter
import com.project.sns.ui.viewmodel.MainViewModel


class HomeFragment : Fragment() {

    private var writeAdapter: WriteAdapter? = null
    private var homeBinding: FragmentHomeBinding? = null
    private var arrayAdapterGenre : ArrayAdapter<String> ?= null
    private var postDataList: ArrayList<PostData> ?= ArrayList()
    private lateinit var database: DatabaseReference
    private var mainViewModel : MainViewModel ?= null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeBinding = FragmentHomeBinding.inflate(layoutInflater)

        return homeBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeBinding?.fab!!.setOnClickListener {
            val intent = Intent(requireContext(), WriteActivity::class.java)
            startActivityForResult(intent, MainActivity.REQUEST_POST)
            requireActivity().overridePendingTransition(com.project.sns.R.anim.visible, com.project.sns.R.anim.invisible);
        }

        writeAdapter = WriteAdapter(requireContext()) { }
        database = Firebase.database.reference
       arrayAdapterGenre = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item)
       arrayAdapterGenre!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        readBoard()

        getUserName()
        homeBinding!!.recyclerSubject.adapter = writeAdapter
        homeBinding!!.recyclerSubject.layoutManager = LinearLayoutManager(context)
    }
    private fun getUserName() {
        val sharedPreference : SharedPreferences = requireContext().getSharedPreferences("Account",MODE_PRIVATE)
        database.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    Log.d("s",sharedPreference.getString("Email", null).equals(user!!.userEmail).toString())
                    Log.d("s", "${sharedPreference.getString("Email", null)} : ${user!!.userEmail}")
                    if(sharedPreference.getString("Email", null).equals(user!!.userEmail)){
                        homeBinding!!.nameOfUser.text = user.userName
                        break
                    }else{
                        homeBinding!!.nameOfUser.text = sharedPreference.getString("userName", "")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    fun dataClear(snapshot: DataSnapshot, requestCode: Int){

        when(requestCode){
            1 -> {
                val dataObject = snapshot.getValue(Genre::class.java)
                arrayAdapterGenre!!.add(dataObject!!.genre)
            }
            2 -> {
                val dataObject = snapshot.getValue(PostData::class.java)
                postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url, dataObject.dateTime, dataObject.genre, dataObject.viewType))
            }
        }

        mainViewModel!!.liveAdapter.value = writeAdapter
    }
    private fun readCategory(){
        //read category ( genre )
        database.child("board").child("genre").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                dataClear(snapshot, 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                dataClear(snapshot, 1)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                dataClear(snapshot, 1)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        //end
    }

    fun readBoard() {
        // read bbs db
        database.child("board").child("path").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                dataClear(snapshot, 2)
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                dataClear(snapshot, 2)
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                dataClear(snapshot, 2)
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", databaseError.message)
            }
        })
        //end
    }

    override fun onStart() {
        readCategory()
        homeBinding?.spinner?.setAdapter(arrayAdapterGenre!!)
        homeBinding?.spinner?.setOnItemSelectedListener { view, position, id, item ->
        }
        super.onStart()
    }
        override fun onStop() {
            mainViewModel!!.liveAdapter.value = writeAdapter
            super.onStop()
        }
}