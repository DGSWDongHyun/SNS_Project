package com.project.sns.ui.fragments.home

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.data.category.Genre
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

        val headerView: View = homeBinding?.navView?.getHeaderView(0)!!
        val drawerImage: ImageView = headerView.findViewById(com.project.sns.R.id.imageHeader) as ImageView
        drawerImage.setImageResource(R.drawable.ic_delete)

        homeBinding?.toolbar?.title = "전체 과목"
        homeBinding?.toolbar?.setTitleTextColor(requireContext().getColor(R.color.white))
        (activity as AppCompatActivity?)!!.setSupportActionBar(homeBinding?.toolbar)

        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeAsUpIndicator(com.project.sns.R.drawable.ic_baseline_menu_24); //뒤로가기 버튼 이미지 지정


        homeBinding?.collapsingToolbarLayout?.setExpandedTitleTextAppearance(com.project.sns.R.style.expanded_toolbar_title);
        homeBinding?.collapsingToolbarLayout?.setCollapsedTitleTextAppearance(com.project.sns.R.style.coll_toolbar_title);

        homeBinding?.fab!!.setOnClickListener {
            val intent = Intent(requireActivity(), WriteActivity::class.java)
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
        database = Firebase.database.reference
       arrayAdapterGenre = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item)
       arrayAdapterGenre!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        readCategory()
        readBoard()

        homeBinding!!.recyclerSubject.adapter = writeAdapter
        homeBinding!!.recyclerSubject.layoutManager = LinearLayoutManager(context)

        homeBinding!!.navView.setNavigationItemSelectedListener {
            it.isChecked = true
            homeBinding!!.drawerLayout.closeDrawers();

            val id = it.itemId

                return@setNavigationItemSelectedListener true
            }
        }


    fun dataClear(snapshot: DataSnapshot, requestCode: Int){

        when(requestCode){
            1 -> {
                val dataObject = snapshot.getValue(Genre::class.java)
                if (dataObject?.isVisible!!) {
                    subMenu?.add(dataObject.genre)
                }
            }
            2 -> {
                if (snapshot.exists()) {
                    val dataObject = snapshot.getValue(PostData::class.java)
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }
            }
        }

        mainViewModel!!.liveAdapter.value = writeAdapter
    }
    private fun readCategory(){
        menu = homeBinding?.navView?.menu!!
        subMenu = menu?.addSubMenu("과목")!!
        //read category ( genre )
        database.child("board").child("genre").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                dataClear(snapshot, 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                subMenu?.clear()
                dataClear(snapshot, 1)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                subMenu?.clear()
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
                postDataList!!.clear()
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
        super.onStart()
    }
        override fun onStop() {
            mainViewModel!!.liveAdapter.value = writeAdapter
            super.onStop()
        }
}