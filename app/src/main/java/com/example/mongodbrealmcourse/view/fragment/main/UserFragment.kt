package com.example.mongodbrealmcourse.view.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentLibraryBinding
import com.example.mongodbrealmcourse.databinding.FragmentUserBinding
import com.example.mongodbrealmcourse.view.activity.MainActivity
import com.example.mongodbrealmcourse.view.fragment.BaseFragment
import com.example.mongodbrealmcourse.viewmodel.callback.VoidCallback
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.example.mongodbrealmcourse.viewmodel.utils.AnimationHelper
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.RealmResultTask
import io.realm.mongodb.mongo.iterable.MongoCursor
import org.bson.Document

class UserFragment : BaseFragment() {
    private var binding: FragmentUserBinding? = null
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
        binding?.tvUserGmail?.text = preferenceHelper.current_account_email
        app = App(AppConfiguration.Builder(Appid).build())
        val user = app.currentUser()
        val queryFilter = Document("email", preferenceHelper.current_account_email )
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("user")

        val findTask: RealmResultTask<MongoCursor<Document>> =
            mongoCollection.find(queryFilter).iterator()
        var dataName = ""
        var dataAddress = ""
        findTask.getAsync { task ->
            if (task.isSuccess) {
                val results: MongoCursor<Document> = task.get()
                if (results.hasNext()) {
                    while (results.hasNext()) {
                        val currentDoc: Document = results.next()
                        var nameUser: String? = currentDoc["name"] as? String
                        var avtUser: String? = currentDoc["avatar"] as? String
                        var addressUser: String? = currentDoc["address"] as? String

                        if (avtUser == null || avtUser == "") {
                            Glide.with(this@UserFragment.requireContext())
                                .load(R.drawable.ic_user)
                                .into(binding?.ivUserAvt!!)
                        } else {
                            Glide.with(this@UserFragment.requireContext())
                                .load(avtUser)
                                .into(binding?.ivUserAvt!!)
                        }
                        if (nameUser == null || nameUser == "") {
                            binding?.tvUserName?.text = getString(R.string.username)
                        } else {
                            for (i in 0 until nameUser.length) {
                                if (dataName == null) {
                                    dataName = ""
                                    dataName = dataName + nameUser[i]
                                } else {
                                    dataName = dataName + nameUser[i]
                                }
                            }
                            binding?.tvUserName?.text = dataName.uppercase()
                        }
                        if (addressUser == null || addressUser == "") {
                            binding?.tvUserAddress?.text = getString(R.string.address_defaul)
                        } else {
                            for (i in 0 until addressUser.length) {
                                if (dataAddress == null) {
                                    dataAddress = ""
                                    dataAddress = dataAddress + addressUser[i]
                                } else {
                                    dataAddress = dataAddress + addressUser[i]
                                }
                            }
                            binding?.tvUserAddress?.text = getString(R.string.address)+" $dataAddress"
                        }
                    }
                }
            } else {
                Log.v("Task Error", task.error.toString())
            }
        }
        binding?.ivEditProfile?.setOnClickListener {
            AnimationHelper.scaleAnimation(it, object : VoidCallback {
                override fun execute() {
                    view.findNavController().navigate(R.id.action_main_fragment_to_editUserFragment)
                }
            }, 0.98f)
        }
        binding?.ivLogOut?.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.confirm_logout))
                .setPositiveButton(getString(R.string.logout)) { dialog, which ->
                    val intent = Intent(this@UserFragment.requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setCancelable(true)

            val dialog = dialogBuilder.create()
            dialog.show()
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