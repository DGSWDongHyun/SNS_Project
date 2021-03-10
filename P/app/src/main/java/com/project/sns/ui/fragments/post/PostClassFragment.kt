package com.project.sns.ui.fragments.post

import android.os.Bundle
import android.text.Editable
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
import com.project.sns.data.board.Genre
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
                clearText()
                alerterShow()

                 }else{
                    Toast.makeText(requireContext(), "경고 내용을 읽어주시고 체크해주시길 바랍니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun alerterShow() {
        Alerter.create(requireActivity())
                .setTitle("띵동!")
                .setText("신청되었습니다! 적용까지는 최대 3일까지 걸릴 수 있습니다.")
                .setBackgroundColorRes(
                        R.color.design_default_color_primary)
                .setDuration(3000)
                .setTitleAppearance(R.style.TextTheme)
                .setTextAppearance(R.style.TextTheme)
                .show()
    }

    private fun clearText() {
        postClassBinding?.titleEditText?.text = Editable.Factory.getInstance().newEditable("")
        postClassBinding?.contentEditText?.text = Editable.Factory.getInstance().newEditable("")
    }

}