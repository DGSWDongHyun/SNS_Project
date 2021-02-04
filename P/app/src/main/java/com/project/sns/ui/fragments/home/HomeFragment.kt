package com.project.sns.ui.fragments.home

import android.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import com.project.sns.ui.activities.ReadActivity
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
    private var menu : Menu ?= null
    var menuItemId = 0
    var users : User ?= null
    private var subMenu : SubMenu ?= null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeBinding = FragmentHomeBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        mainViewModel?.fragmentView?.value = homeBinding
        return homeBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("Account", Context.MODE_PRIVATE)
        database = Firebase.database.reference

        val headerView: View = homeBinding?.navView?.getHeaderView(0)!!
        val nameText = headerView.findViewById<TextView>(com.project.sns.R.id.name)
        val emailText = headerView.findViewById<TextView>(com.project.sns.R.id.email)

        getUserName(nameText)
        emailText.text = sharedPreferences.getString("Email", "")
        
        homeBinding?.toolbar?.setTitleTextColor(requireContext().getColor(R.color.white))
        (activity as AppCompatActivity?)!!.setSupportActionBar(homeBinding?.toolbar)
        
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "전체 과목"
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeAsUpIndicator(com.project.sns.R.drawable.ic_baseline_menu_24); //뒤로가기 버튼 이미지 지정

        homeBinding?.collapsingToolbarLayout?.setExpandedTitleTextAppearance(com.project.sns.R.style.expanded_toolbar_title);
        homeBinding?.collapsingToolbarLayout?.setCollapsedTitleTextAppearance(com.project.sns.R.style.coll_toolbar_title);
        homeBinding?.collapsingToolbarLayout?.isTitleEnabled = false

        homeBinding?.fab!!.setOnClickListener {
            val intent = Intent(requireActivity(), WriteActivity::class.java)
            intent.putExtra("userName", users?.userName)
            requireActivity().startActivityForResult(intent, MainActivity.REQUEST_POST)
            requireActivity().overridePendingTransition(com.project.sns.R.anim.visible, com.project.sns.R.anim.invisible);
        }

        writeAdapter = WriteAdapter(requireContext()) { position: Int, listPostData: List<PostData> -> 
            val intent = Intent(requireActivity(), ReadActivity::class.java)
            intent.putExtra("title", listPostData[position].title)
            intent.putExtra("content", listPostData[position].content)
            intent.putExtra("genre", listPostData[position].genre)
            intent.putExtra("key", listPostData[position].key)
            intent.putExtra("commentCount", listPostData[position].commentCount)
            intent.putExtra("image", listPostData[position].image_url)
            intent.putExtra("userName", listPostData[position].UserName)
            startActivity(intent)
            requireActivity().overridePendingTransition(com.project.sns.R.anim.pull_anim, com.project.sns.R.anim.invisible);
        }

       arrayAdapterGenre = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item)
       arrayAdapterGenre!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        readCategory()
        readBoard()

        homeBinding!!.recyclerSubject.adapter = writeAdapter
        homeBinding!!.recyclerSubject.layoutManager = LinearLayoutManager(context)

        homeBinding!!.navView.setNavigationItemSelectedListener {

            Log.d("TAG", it.title.toString() + "${(activity as AppCompatActivity?)!!.supportActionBar?.title}")

            (activity as AppCompatActivity?)!!.supportActionBar?.title = it.title.toString()
            findBoard(it.title.toString())

            homeBinding!!.drawerLayout.closeDrawers();

            return@setNavigationItemSelectedListener true
            }
        }

    private fun getUserName(nameText: TextView) {
        val sharedPreference : SharedPreferences = requireContext().getSharedPreferences("Account", Context.MODE_PRIVATE)
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

    fun dataClear(snapshot: DataSnapshot, requestCode: Int){
        when(requestCode){
            1 -> {
                val dataObject = snapshot.getValue(Genre::class.java)
                if (dataObject?.isVisible!!) {
                    subMenu?.add(0, menuItemId, 0, dataObject.genre)
                    menuItemId++
                }
            }
            2 -> {
                val dataObject = snapshot.getValue(PostData::class.java)
                postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                        dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                        dataObject.commentList, dataObject.key))
            }
        }

        mainViewModel!!.liveAdapter.value = writeAdapter
    }
    private fun findBoard(title : String){
        // read bbs db
        postDataList?.clear()

        database.child("board").child("path").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val dataObject = snapshot.getValue(PostData::class.java)
                if(dataObject?.genre == title){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }
                if(title == "전체 과목"){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }

                writeAdapter!!.setData(postDataList)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                postDataList?.clear()
                val dataObject = snapshot.getValue(PostData::class.java)
                if(dataObject?.genre == title){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }

                if(title == "전체 과목"){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                postDataList?.clear()
                val dataObject = snapshot.getValue(PostData::class.java)
                if(dataObject?.genre == title){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }

                if(title == "전체 과목"){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }
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
    private fun readCategory(){
        menu = homeBinding?.navView?.menu!!
        subMenu = menu?.addSubMenu("과목")!!
        subMenu?.add("전체 과목")
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

    private fun readBoard() {
        // read bbs db
        database.child("board").child("path").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                dataClear(snapshot, 2)
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                postDataList?.clear()
                val dataObject = snapshot.getValue(PostData::class.java)
                findBoard(dataObject?.genre!!)
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                postDataList?.clear()
                val dataObject = snapshot.getValue(PostData::class.java)
                findBoard(dataObject?.genre!!)
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
        super.onStart()
    }
        override fun onStop() {
            mainViewModel!!.liveAdapter.value = writeAdapter
            super.onStop()
        }
}