package com.project.sns.ui.sheet

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.arthurivanets.bottomsheets.BaseBottomSheet
import com.arthurivanets.bottomsheets.config.BaseConfig
import com.arthurivanets.bottomsheets.config.Config
import com.project.sns.R
import com.project.sns.data.board.Comment
import com.project.sns.data.board.PostData
import com.project.sns.ui.viewmodel.MainViewModel

class DetailBottomSheet(hostActivity : Activity, config : BaseConfig = Config.Builder(hostActivity).build()) : BaseBottomSheet(hostActivity, config) {

    override fun onCreateSheetContentView(context : Context) : View {


        val view = LayoutInflater.from(context).inflate(
                R.layout.view_bottomsheet,
                this,
                false
        )

        val textBottomData = view.findViewById<TextView>(R.id.userName)

        return view
    }

}