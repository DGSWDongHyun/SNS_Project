package com.project.sns.ui.viewmodel

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.project.sns.data.board.User
import com.project.sns.databinding.FragmentHomeBinding
import com.project.sns.databinding.FragmentProfileBinding
import com.project.sns.ui.adapters.WriteAdapter


class MainViewModel : ViewModel() {
    val liveAdapter : MutableLiveData<WriteAdapter> = MutableLiveData()
    var fragmentView : MutableLiveData<FragmentHomeBinding> = MutableLiveData()
    var fragmentViewProfile : MutableLiveData<FragmentProfileBinding> = MutableLiveData()
    var key : MutableLiveData<String> = MutableLiveData()
    var data : MutableLiveData<Uri> = MutableLiveData()
    var userAccount : MutableLiveData<User> = MutableLiveData()
}