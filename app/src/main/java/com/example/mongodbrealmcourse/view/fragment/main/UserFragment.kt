package com.example.mongodbrealmcourse.view.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentLibraryBinding
import com.example.mongodbrealmcourse.databinding.FragmentUserBinding
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener

class UserFragment : Fragment() {
    private var binding: FragmentUserBinding? = null
    private var homeListener: HomeListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentUserBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ivEditProfile?.setOnClickListener {
            view.findNavController().navigate(R.id.action_main_fragment_to_editUserFragment)
        }
    }
    companion object {
        fun newInstance(homeListener: HomeListener?): UserFragment {
            val fragment = UserFragment()
            fragment.setListener(homeListener)
            return fragment
        }
    }
    fun setListener(homeListener: HomeListener?) {
        this.homeListener = homeListener
    }

}