package com.project.sns.ui.fragments.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.sns.R
import com.project.sns.databinding.FragmentPostClassBinding
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import com.tapadoo.alerter.OnShowAlertListener


class PostClassFragment : Fragment() {
    var postClassBinding : FragmentPostClassBinding ?= null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        postClassBinding = FragmentPostClassBinding.inflate(layoutInflater)

        return postClassBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postClassBinding?.fabAcess?.setOnClickListener {
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
        }
    }


}