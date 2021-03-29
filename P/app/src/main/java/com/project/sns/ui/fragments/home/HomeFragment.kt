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
import com.project.sns.data.board.Genre
import com.project.sns.data.board.PostData
import com.project.sns.data.board.User
import com.project.sns.data.module.FirebaseDatabaseModule
import com.project.sns.data.module.SimpleIntentModule
import com.project.sns.databinding.FragmentHomeBinding
import com.project.sns.ui.activities.MainActivity
import com.project.sns.ui.activities.write.ReadActivity
import com.project.sns.ui.activities.write.WriteActivity
import com.project.sns.ui.adapters.WriteAdapter
import com.project.sns.ui.adapters.listener.onClickItemListener
import com.project.sns.ui.viewmodel.MainViewModel
import kotlinx.coroutines.*


class HomeFragment : Fragment() {

    private var writeAdapter: WriteAdapter? = null
    private var homeBinding: FragmentHomeBinding? = null
    private var arrayAdapterGenre : ArrayAdapter<String> ?= null
    private lateinit var database: DatabaseReference
    private var mainViewModel : MainViewModel ?= null
    var users : User ?= null

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



        GlobalScope.launch {
            initRecyclerViews()
        }

        initLayout(sharedPreferences)

    }

    private fun initLayout(sharedPreferences : SharedPreferences) {
        val headerView: View = homeBinding?.navView?.getHeaderView(0)!!
        val nameText = headerView.findViewById<TextView>(com.project.sns.R.id.name)
        val emailText = headerView.findViewById<TextView>(com.project.sns.R.id.email)

        FirebaseDatabaseModule.getUserName(nameText, requireContext())
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

        homeBinding?.swipeLayout!!.setOnRefreshListener {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    FirebaseDatabaseModule.readBoard(writeAdapter!!)
                    delay(1500)
                    homeBinding?.swipeLayout!!.isRefreshing = false
                }
            }
        }



        homeBinding!!.navView.setNavigationItemSelectedListener {

            Log.d("TAG", it.title.toString() + "${(activity as AppCompatActivity?)!!.supportActionBar?.title}")

            (activity as AppCompatActivity?)!!.supportActionBar?.title = it.title.toString()
            FirebaseDatabaseModule.findBoard(it.title.toString(), writeAdapter!!)

            homeBinding!!.drawerLayout.closeDrawers();

            return@setNavigationItemSelectedListener true
        }
    }
    private suspend fun initRecyclerViews() {
        arrayAdapterGenre = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item)
        arrayAdapterGenre!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        writeAdapter = WriteAdapter(requireContext(), object : onClickItemListener {
            override fun onClickItem(position: Int, postData: ArrayList<PostData>) {
               SimpleIntentModule.simplyActivity(requireActivity(), position, postData)
            }
        })

        writeAdapter?.clearData()

        val jobCompleted : Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
            val job : Job = GlobalScope.async {
                FirebaseDatabaseModule.readBoard(writeAdapter!!)
                FirebaseDatabaseModule.readCategory(homeBinding!!)
            }

            job.join()

            true
        }

        jobCompleted.await()

        if(jobCompleted.isCompleted) {
            homeBinding!!.recyclerBoard.layoutManager = LinearLayoutManager(context)
            homeBinding!!.recyclerBoard.adapter = writeAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch {
            FirebaseDatabaseModule.readBoard(writeAdapter!!)
        }
    }

    override fun onStart() {
        super.onStart()
    }
        override fun onStop() {
            writeAdapter!!.clearData()
            Log.d("onStop", "onStop")
            super.onStop()
        }
}