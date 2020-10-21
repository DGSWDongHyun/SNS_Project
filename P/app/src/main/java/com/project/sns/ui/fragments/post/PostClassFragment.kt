package com.project.sns.ui.fragments.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.sns.databinding.FragmentPostClassBinding


class PostClassFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = FragmentPostClassBinding.inflate(layoutInflater)



        return view.root
    }


}