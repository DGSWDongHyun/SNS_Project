package com.project.sns.ui.fragments.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.R
import com.project.sns.data.category.Genre
import com.project.sns.databinding.FragmentPostClassBinding
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import com.tapadoo.alerter.OnShowAlertListener


class PostClassFragment : Fragment() {
    var database : DatabaseReference ?= null
    var postClassBinding : FragmentPostClassBinding ?= null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        postClassBinding = FragmentPostClassBinding.inflate(layoutInflater)

        return postClassBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postClassBinding?.fabAcess?.setOnClickListener {
            if(postClassBinding?.checkBox?.isChecked!!){
                database = Firebase.database.reference

                database?.child("board")?.child("genre")?.push()?.setValue(Genre(postClassBinding?.titleEditText?.text.toString(), false))

                Alerter.create(requireActivity())
                        .setTitle("띵동!")
                        .setText("신청되었습니다! 적용까지는 최대 3일까지 걸릴 수 있습니다.")
                        .setBackgroundColorRes(
                                R.color.design_default_color_primary)
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
                    Toast.makeText(requireContext(), "경고 내용을 읽어주시고 체크해주시길 바랍니다.", Toast.LENGTH_LONG).show()
            }
        }
    }


}