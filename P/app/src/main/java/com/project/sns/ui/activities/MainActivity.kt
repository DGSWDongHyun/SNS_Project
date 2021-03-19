package com.project.sns.ui.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.sns.GlideApp
import com.project.sns.R
import com.project.sns.data.board.User
import com.project.sns.data.module.*
import com.project.sns.databinding.ActivityMainBinding
import com.project.sns.databinding.FragmentHomeBinding
import com.project.sns.ui.activities.splash.IntroActivity
import com.project.sns.ui.activities.write.WriteActivity
import com.project.sns.ui.viewmodel.MainViewModel
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import com.tapadoo.alerter.OnShowAlertListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    var mainBinding : ActivityMainBinding ?= null
    var mainViewModel : MainViewModel ?= null
    lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)

        initCallback() // viewModel, database initialize.
        startActivity(Intent(this, IntroActivity::class.java)) // call loading screen.
        initLayout() // initLayout
    }

    private fun initCallback(){
        database = Firebase.database.reference

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun initLayout(){
        //init navView.
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navView, navController)
        //end. - navView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_POST -> {
                showAlerter(resultCode)
            }
        }
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
                .setTitle("종료하시겠습니까?")
                .setPositiveButton("종료") { dialogInterface: DialogInterface, i: Int ->
                    finish()
                }.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->

                }

        alertDialog.create().show()
    }

    private fun showAlerter(resultCode: Int) {
        if(resultCode == RESULT_OK){
            Alerter.create(this@MainActivity)
                    .setTitle("띵동!")
                    .setText("글쓰기가 완료되었습니다!")
                    .setBackgroundColorRes(
                            R.color.alerter_default_success_background)
                    .setDuration(3000)
                    .setTitleAppearance(R.style.TextTheme)
                    .setTextAppearance(R.style.TextTheme)
                    .show()

            GlobalScope.launch {
                 withContext(Dispatchers.IO) {
                     FirebaseDatabaseModule.sendNotification()
                 }
            }
        }else{
            Alerter.create(this@MainActivity)
                    .setTitle("띵동!")
                    .setText("글쓰기가 취소되었습니다!")
                    .setBackgroundColorRes(
                            R.color.design_default_color_error)
                    .setDuration(3000)
                    .setTitleAppearance(R.style.TextTheme)
                    .setTextAppearance(R.style.TextTheme)
                    .show()
        }
    }
    companion object{
        const val REQUEST_POST = 100
        const val FROM_ALBUM = 2
    }
}