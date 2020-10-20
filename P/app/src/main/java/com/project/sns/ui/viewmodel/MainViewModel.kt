package com.project.sns.ui.viewmodel

import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.project.sns.data.write.PostData
import com.project.sns.ui.adapters.WriteAdapter


class MainViewModel : ViewModel() {
    var chartData : MutableLiveData<PieEntry> = MutableLiveData()
    val liveAdapter : MutableLiveData<WriteAdapter> = MutableLiveData()
}