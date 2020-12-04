package com.project.sns.ui.fragments.profile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.project.sns.GlideApp
import com.project.sns.data.user.User
import com.project.sns.databinding.FragmentProfileBinding
import com.project.sns.ui.activities.write.WriteActivity
import com.project.sns.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

open class ProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private var mainViewModel : MainViewModel?= null
    var profileBinding : FragmentProfileBinding ?= null
    var key : String ?= ""

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        profileBinding = FragmentProfileBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        database = Firebase.database.reference

        return profileBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileBinding!!.chart1.description.isEnabled = false

        profileBinding!!.chart1.setCenterTextSize(10f)

        profileBinding!!.chart1.holeRadius = 45f
        profileBinding!!.chart1.transparentCircleRadius = 50f

        profileBinding!!.chart1.data = generatePieData()

        getUserName()

        profileBinding?.imageProfile?.setOnClickListener {
            selectAlbum()
        }
    }

    private fun selectAlbum() {
        mainViewModel?.fragmentViewProfile?.value = profileBinding
        mainViewModel?.key?.value = key!!
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        startActivityForResult(intent, WriteActivity.FROM_ALBUM)
    }


    private fun getUserName() {
        val sharedPreference : SharedPreferences = requireContext().getSharedPreferences("Account", Context.MODE_PRIVATE)
        database.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    Log.d("s", sharedPreference.getString("Email", null).equals(user!!.userEmail).toString())
                    Log.d("s", "${sharedPreference.getString("Email", null)} : ${user!!.userEmail}")
                    if (sharedPreference.getString("Email", null).equals(user!!.userEmail)) {
                        profileBinding!!.name.text = user.userName + "님의 최근 공부 시간입니다."
                        profileBinding!!.nameText.text = "이름 : " + user.userName
                        key = user.key
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun generatePieData(): PieData? {
        val count = 2
        val value : Float = 50F
        val entries1: ArrayList<PieEntry> = ArrayList()
        for (i in 0 until count) {
            entries1.add(PieEntry(value, "과목 " + (i + 1)))
        }
        val ds1 = PieDataSet(entries1, "최근 공부 시간")
        ds1.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        ds1.sliceSpace = 2f
        ds1.valueTextColor = Color.WHITE
        ds1.valueTextSize = 12f
        val d = PieData(ds1)

        return d
    }
}