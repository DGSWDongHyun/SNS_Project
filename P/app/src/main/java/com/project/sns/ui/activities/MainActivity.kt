package com.project.sns.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.R
import com.project.sns.data.write.PostData
import com.project.sns.databinding.ActivityMainBinding
import com.project.sns.ui.activities.write.WriteActivity
import com.project.sns.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    var mainBinding : ActivityMainBinding ?= null
    var mainViewModel : MainViewModel ?= null
    var postList : List<PostData> = arrayListOf()

    private val REQUEST_POST = 100
    lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)
        database = Firebase.database.reference
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initLayout();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_POST){
            if(resultCode == RESULT_OK){
                Toast.makeText(applicationContext,"글쓰기가 성공적으로 이루어졌습니다.", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext,"글쓰기를 취소하셨습니다.", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun initLayout(){

        //init navView.
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navView, navController)
        //end. - navView

        mainBinding?.fab!!.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
            startActivityForResult(intent, REQUEST_POST)
            overridePendingTransition(R.anim.visible, R.anim.invisible);
        }

        if(mainViewModel!!.liveAdapter.value?.getData() != null)
            postList = mainViewModel!!.liveAdapter.value!!.getData()




    }
}