package com.project.sns.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.sns.R
import com.project.sns.databinding.ActivityReadBinding

class ReadActivity : AppCompatActivity() {
    var readBinding : ActivityReadBinding ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readBinding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(readBinding?.root)


    }
}