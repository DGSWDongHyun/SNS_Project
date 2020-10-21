package com.project.sns.ui.fragments.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.project.sns.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = FragmentProfileBinding.inflate(layoutInflater)

        view.chart1.getDescription().setEnabled(false)

        view.chart1.setCenterTextSize(10f)

        view.chart1.setHoleRadius(45f)
        view.chart1.setTransparentCircleRadius(50f)


        view.chart1.setData(generatePieData())

        return view.root
    }

    protected fun generatePieData(): PieData? {
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