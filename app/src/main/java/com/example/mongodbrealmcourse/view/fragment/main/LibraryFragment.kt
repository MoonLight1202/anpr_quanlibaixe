package com.example.mongodbrealmcourse.view.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.view.activity.ScanQRActivity
import com.example.mongodbrealmcourse.view.adapter.PlateLibAdapter
import com.example.mongodbrealmcourse.databinding.FragmentLibraryBinding
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.example.mongodbrealmcourse.model.`object`.PlateNumberObject
import com.example.mongodbrealmcourse.view.activity.MainActivity
import com.example.mongodbrealmcourse.viewmodel.listener.HomeFragmentListener
import com.google.zxing.integration.android.IntentIntegrator
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.mongo.iterable.MongoCursor
import org.bson.Document


class LibraryFragment : Fragment() {
    private var homeListener: HomeListener? = null
    private var binding: FragmentLibraryBinding? = null
    private val Appid = "number-plates-ayumd"
    private lateinit var app: App
    private lateinit var adapter: PlateLibAdapter
    private lateinit var mongoDatabase: MongoDatabase
    private lateinit var mongoClient: MongoClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentLibraryBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    companion object {
        fun newInstance(homeListener: HomeListener? ): LibraryFragment {
            val fragment = LibraryFragment()
            fragment.setListener(homeListener)
            return fragment
        }
    }

    fun setListener(homeListener: HomeListener?) {
        this.homeListener = homeListener
        loadData()
    }

    fun loadData() {
        app = App(AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")
        val findAllTask = mongoCollection.find().iterator()
        val plateNumberList = mutableListOf<PlateNumberObject>()

        findAllTask.getAsync { task ->
            if (task.isSuccess) {
                val results: MongoCursor<Document> = task.get()
                if (!results.hasNext()) {
                    Toast.makeText(
                        this@LibraryFragment.requireContext(),
                        "Không có phần tử nào",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                while (results.hasNext()) {
                    val currentDoc: Document = results.next()
                    var id_user: String? = currentDoc["id_user"] as? String
                    var info_plate: String? = currentDoc["info_plate"] as? String
                    var region: String? = currentDoc["region"] as? String
                    var type_car: String? = currentDoc["type_car"] as? String
                    var pay: String? = currentDoc["pay"] as? String
                    var accuracy: String? = currentDoc["accuracy"] as? String
                    var img_plate: String? = currentDoc["img_plate"] as? String
                    var img_plate_cut: String? = currentDoc["img_plate_cut"] as? String
                    var time_create: String? = currentDoc["time_create"] as? String

                    val plateNumberObject = PlateNumberObject(
                        id_user = id_user ?: "",
                        infoPlate = info_plate ?: "",
                        region = region ?: "",
                        typeCar = type_car ?: "",
                        pay = pay ?: "",
                        accuracy = accuracy?.toFloatOrNull() ?: 0.0f,
                        imgPlate = img_plate ?: "",
                        imgPlateCut = img_plate_cut ?: "",
                        dateCreate = time_create ?: ""
                    )

                    plateNumberList.add(plateNumberObject)
                    Log.d(
                        "TAG_H",
                        id.toString() + "***" + info_plate + "***" + region + "***" + type_car + "***" + accuracy + "***" + time_create
                    )
                }
                adapter.submitList(plateNumberList)
                adapter.notifyDataSetChanged()
            } else {
                Log.v("Task Error", task.error.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Cancelled from fragment", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Result: " + result.contents, Toast.LENGTH_LONG).show()
                val bundle = Bundle()
                bundle.putString(
                    "key",
                    result.contents
                ) // Thay "key" và "value" bằng dữ liệu bạn muốn truyền
                view?.findNavController()?.navigate(R.id.action_main_fragment_to_result_qr_fragment2, bundle)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlateLibAdapter()
        binding?.apply {
            btnScan.setOnClickListener {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this@LibraryFragment)
                intentIntegrator.captureActivity = ScanQRActivity::class.java
                intentIntegrator.setBeepEnabled(false)
                intentIntegrator.setCameraId(0)
                intentIntegrator.setPrompt("SCAN")
                intentIntegrator.setBarcodeImageEnabled(false)
                intentIntegrator.initiateScan()
            }
            // Set LayoutManager
            rcvLib.layoutManager = LinearLayoutManager(this@LibraryFragment.requireContext())

            // Set Adapter
            rcvLib.adapter = adapter

            // Sample data
            Realm.init(this@LibraryFragment.requireContext())

            app = App(AppConfiguration.Builder(Appid).build())

            val user = app.currentUser()
            if (user != null) {
                mongoClient = user.getMongoClient("mongodb-atlas")
            }
            mongoDatabase = mongoClient.getDatabase("number-plates-data")
            val mongoCollection = mongoDatabase.getCollection("infoPlate")
            val findAllTask = mongoCollection.find().iterator()
            val plateNumberList = mutableListOf<PlateNumberObject>()

            findAllTask.getAsync { task ->
                if (task.isSuccess) {
                    val results: MongoCursor<Document> = task.get()
                    if (!results.hasNext()) {
                        Toast.makeText(
                            this@LibraryFragment.requireContext(),
                            "Không có phần tử nào",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    while (results.hasNext()) {
                        val currentDoc: Document = results.next()
                        var id_user: String? = currentDoc["id_user"] as? String
                        var info_plate: String? = currentDoc["info_plate"] as? String
                        var region: String? = currentDoc["region"] as? String
                        var type_car: String? = currentDoc["type_car"] as? String
                        var pay: String? = currentDoc["pay"] as? String
                        var accuracy: String? = currentDoc["accuracy"] as? String
                        var img_plate: String? = currentDoc["img_plate"] as? String
                        var img_plate_cut: String? = currentDoc["img_plate_cut"] as? String
                        var time_create: String? = currentDoc["time_create"] as? String

                        val plateNumberObject = PlateNumberObject(
                            id_user = id_user ?: "",
                            infoPlate = info_plate ?: "",
                            region = region ?: "",
                            typeCar = type_car ?: "",
                            pay =pay ?: "",
                            accuracy = accuracy?.toFloatOrNull() ?: 0.0f,
                            imgPlate = img_plate ?: "",
                            imgPlateCut = img_plate_cut ?: "",
                            dateCreate = time_create ?: ""
                        )

                        plateNumberList.add(plateNumberObject)

                        Log.d(
                            "TAG_H",
                            id.toString() + "***" + info_plate + "***" + region + "***" + type_car + "***" + accuracy + "***" + time_create
                        )
                    }
                    adapter.submitList(plateNumberList)
                } else {
                    Log.v("Task Error", task.error.toString())
                }
            }


        }
    }
}