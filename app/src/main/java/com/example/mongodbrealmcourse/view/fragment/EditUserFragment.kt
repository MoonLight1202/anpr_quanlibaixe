package com.example.mongodbrealmcourse.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentEditUserBinding
import com.example.mongodbrealmcourse.viewmodel.callback.VoidCallback
import com.example.mongodbrealmcourse.viewmodel.utils.AnimationHelper
import com.example.mongodbrealmcourse.viewmodel.utils.PreferenceHelper
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.RealmResultTask
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.mongo.iterable.MongoCursor
import org.bson.Document

class EditUserFragment : BaseFragment() {
    private var binding: FragmentEditUserBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentEditUserBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        var addressUser: String? = currentDoc["address"] as? String

                        if (nameUser == null) {
                            binding?.edtUsername?.hint = getString(R.string.enter_full_name)
                        } else {
                            for (i in 0 until nameUser.length) {
                                if (dataName == null) {
                                    dataName = ""
                                    dataName = dataName + nameUser[i]
                                } else {
                                    dataName = dataName + nameUser[i]
                                }
                            }
                            binding?.edtUsername?.hint = dataName
                        }
                        if (addressUser == null) {
                            binding?.editAddress?.hint = getString(R.string.enter_address)
                        } else {
                            for (i in 0 until addressUser.length) {
                                if (dataAddress == null) {
                                    dataAddress = ""
                                    dataAddress = dataAddress + addressUser[i]
                                } else {
                                    dataAddress = dataAddress + addressUser[i]
                                }
                            }
                            binding?.editAddress?.hint = dataAddress
                        }
                    }
                }
            } else {
                Log.v("Task Error", task.error.toString())
            }
        }

        binding?.saveBtn?.setOnClickListener {
            AnimationHelper.scaleAnimation(it, object : VoidCallback {
                override fun execute() {
                    val name = binding?.edtUsername?.text?.toString()
                    val address = binding?.editAddress?.text?.toString()

                    val updateFilter = Document("email", preferenceHelper.current_account_email)
                    val updateDocument = Document("\$set", Document("name", name).append("address", address))

                    mongoCollection.updateOne(updateFilter, updateDocument).getAsync { task ->
                        if (task.isSuccess) {
                            Toast.makeText(requireContext(), getString(R.string.update_successful), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                            Log.v("Update Error", task.error.toString())
                        }
                    }
                }
            }, 0.98f)
        }
        binding?.btnBackProfileEdit?.setOnClickListener {
            AnimationHelper.scaleAnimation(it, object : VoidCallback {
                override fun execute() {
                    findNavController().popBackStack()
                }
            }, 0.98f)

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}