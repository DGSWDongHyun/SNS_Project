package com.project.sns.ui.activities.register.ui.password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.project.sns.R
import com.project.sns.databinding.FragmentForgotBinding

class ForgotFragment : Fragment() {

    var forgotBinding : FragmentForgotBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        forgotBinding = FragmentForgotBinding.inflate(layoutInflater)
        return forgotBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            initLayout();
    }
    private fun initLayout(){

    }
}