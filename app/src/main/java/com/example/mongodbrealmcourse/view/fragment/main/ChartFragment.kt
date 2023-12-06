package com.example.mongodbrealmcourse.view.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener

class ChartFragment : Fragment() {
    private var homeListener: HomeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }
    companion object {
        fun newInstance(homeListener: HomeListener?): ChartFragment {
            val fragment = ChartFragment()
            fragment.setListener(homeListener)
            return fragment
        }
    }
    fun setListener(homeListener: HomeListener?) {
        this.homeListener = homeListener
    }
}