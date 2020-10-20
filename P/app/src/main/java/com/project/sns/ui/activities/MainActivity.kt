package com.project.sns.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.sns.R
import com.project.sns.data.write.PostData
import com.project.sns.databinding.ActivityMainBinding
import com.project.sns.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    var mainBinding : ActivityMainBinding ?= null
    var mainViewModel : MainViewModel ?= null
    var postList : List<PostData> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initLayout();
    }
    private fun initLayout(){

        //init navView.
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navView, navController)
        //end. - navView

        if(mainViewModel!!.liveAdapter.value?.getData() != null)
            postList = mainViewModel!!.liveAdapter.value!!.getData()




    }
}