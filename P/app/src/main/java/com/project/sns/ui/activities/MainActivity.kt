package com.project.sns.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)

        initCallback() // viewModel, database initialize.
        startActivity(Intent(this, IntroActivity::class.java)) // call loading screen.
        initLayout() // initLayout
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mainViewModel?.fragmentView?.value?.drawerLayout?.openDrawer(Gravity.START)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
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
        when (requestCode) {
            REQUEST_POST -> {
                showAlerter(resultCode)
            }
            WriteActivity.FROM_ALBUM -> {
                if (data?.data != null) {
                    try {
                        val photoURI = data.data
                        mainViewModel?.data?.value = data.data
                        GlideApp.with(this).load(photoURI).into(mainViewModel?.fragmentViewProfile?.value?.imageProfile!!)
                        makeConfirmDialog(mainViewModel?.data?.value, WriteActivity.FROM_ALBUM)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    private fun makeConfirmDialog(data: Uri?, flag: Int) {

        val filename = "uploaded" + "_" + System.currentTimeMillis()
        val storage : FirebaseStorage?= FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage!!.reference.child("profiles/$filename")
        val uploadTask: UploadTask
        var file: Uri? = null
        if (flag == WriteActivity.TAKE_PHOTO) {
        } else if (flag == WriteActivity.FROM_ALBUM) {
            file = data
        }
        uploadTask = storageRef.putFile(file!!)
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("업로드중...")
        progressDialog.show()

        uploadTask.addOnFailureListener(OnFailureListener {
            exception ->  exception.printStackTrace()
        }).addOnSuccessListener(OnSuccessListener<Any> { taskSnapshot ->

        })
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