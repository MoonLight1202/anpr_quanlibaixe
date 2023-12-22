package com.example.mongodbrealmcourse.view.fragment.main

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentMoreBinding
import com.example.mongodbrealmcourse.view.fragment.BaseFragment
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import java.util.Locale

class MoreFragment : BaseFragment() {
    private var binding: FragmentMoreBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentMoreBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerValues = listOf("Tiếng Việt", "English")
        val adapter = ArrayAdapter(this@MoreFragment.requireContext(), R.layout.support_simple_spinner_dropdown_item, spinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spnLanguage?.adapter = adapter
        if(preferenceHelper.current_language == "vi") binding?.spnLanguage?.setSelection(0) else binding?.spnLanguage?.setSelection(1)
        binding?.spnLanguage?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValue = binding?.spnLanguage?.getItemAtPosition(position) as String
                val locale = if (selectedValue == "Tiếng Việt") {
                    Locale("vi")
                } else {
                    Locale("en")
                }
                if(locale.toString() != preferenceHelper.current_language) {
                    preferenceHelper.current_language = locale.toString()
                    Log.d("TAG_UU", preferenceHelper.current_language)
                    val intent =
                        requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent?.let {
                        startActivity(it)
                        requireActivity().finish()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding?.layoutTerm?.setOnClickListener {
            view.findNavController().navigate(R.id.action_main_fragment_to_termFragment)
        }
        binding?.layoutHelp?.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:hoangminhtuyen120201@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Suggestions on license plate recognition applications")
                putExtra(Intent.EXTRA_TEXT, "Content of the email")
            }
            startActivity(emailIntent)
        }
    }



    companion object {
        fun newInstance(homeListener: HomeListener?): MoreFragment {
            val fragment = MoreFragment()
            fragment.setListener(homeListener)
            return fragment
        }
    }
    fun setListener(homeListener: HomeListener?) {
        this.homeListener = homeListener
    }
}