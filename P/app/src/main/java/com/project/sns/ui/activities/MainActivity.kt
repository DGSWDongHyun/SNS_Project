package com.project.sns.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.R
import com.project.sns.data.write.PostData
import com.project.sns.databinding.ActivityMainBinding
import com.project.sns.databinding.FragmentHomeBinding
import com.project.sns.ui.activities.splash.IntroActivity
import com.project.sns.ui.activities.write.WriteActivity
import com.project.sns.ui.viewmodel.MainViewModel
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import com.tapadoo.alerter.OnShowAlertListener


class MainActivity : AppCompatActivity() {

    var mainBinding : ActivityMainBinding ?= null
    var mainViewModel : MainViewModel ?= null
    lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if(requestCode == REQUEST_POST){
            showAlerter(resultCode)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun showAlerter(resultCode: Int) {
        if(resultCode == RESULT_OK){
            Alerter.create(this@MainActivity)
                    .setTitle("띵동!")
                    .setText("글쓰기가 완료되었습니다!")
                    .setIcon(getDrawable(R.drawable.checked)!!)
                    .setBackgroundColorRes(
                            R.color.alerter_default_success_background)
                    .setDuration(3000)
                    .setTitleAppearance(R.style.TextTheme)
                    .setTextAppearance(R.style.TextTheme)
                    .setOnClickListener(
                            View.OnClickListener {
                                // do something when
                                // Alerter message was clicked
                            })
                    .setOnShowListener(
                            OnShowAlertListener {
                                // do something when
                                // Alerter message shows
                            })
                    .setOnHideListener(
                            OnHideAlertListener {
                                // do something when
                                // Alerter message hides
                            })
                    .show()
        }else{
            Alerter.create(this@MainActivity)
                    .setTitle("띵동!")
                    .setText("글쓰기가 취소되었습니다!")
                    .setIcon(getDrawable(R.drawable.checked)!!)
                    .setBackgroundColorRes(
                            R.color.design_default_color_error)
                    .setDuration(3000)
                    .setTitleAppearance(R.style.TextTheme)
                    .setTextAppearance(R.style.TextTheme)
                    .setOnClickListener(
                            View.OnClickListener {
                                // do something when
                                // Alerter message was clicked
                            })
                    .setOnShowListener(
                            OnShowAlertListener {
                                // do something when
                                // Alerter message shows
                            })
                    .setOnHideListener(
                            OnHideAlertListener {
                                // do something when
                                // Alerter message hides
                            })
                    .show()
        }
    }
    companion object{
        const val REQUEST_POST = 100
    }
}