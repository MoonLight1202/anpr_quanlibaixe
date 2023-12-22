package com.example.mongodbrealmcourse.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mongodbrealmcourse.databinding.FragmentLanguageBinding

class LanguageFragment : BaseFragment() {
    private var binding: FragmentLanguageBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentLanguageBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }
}