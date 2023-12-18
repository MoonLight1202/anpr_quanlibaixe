package com.example.mongodbrealmcourse.view.fragment.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.view.activity.ScanQRActivity
import com.example.mongodbrealmcourse.view.adapter.PlateLibAdapter
import com.example.mongodbrealmcourse.databinding.FragmentLibraryBinding
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.example.mongodbrealmcourse.model.`object`.PlateNumberObject
import com.example.mongodbrealmcourse.view.fragment.BaseFragment
import com.example.mongodbrealmcourse.viewmodel.callback.VoidCallback
import com.example.mongodbrealmcourse.viewmodel.utils.AnimationHelper
import com.google.zxing.integration.android.IntentIntegrator
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.RealmResultTask
import io.realm.mongodb.mongo.iterable.MongoCursor
import org.bson.Document
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LibraryFragment : BaseFragment() {
    private var binding: FragmentLibraryBinding? = null
    private lateinit var adapter: PlateLibAdapter


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
    }

    fun loadData(id_user: String) {
        app = App(AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")
        val query = Document("id_user",id_user)
        val findAllTask = mongoCollection.find(query).sort(Document("time_create", -1)).iterator()
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
                    var cost: Int? = currentDoc["cost"] as? Int
                    var accuracy: String? = currentDoc["accuracy"] as? String
                    var img_plate: String? = currentDoc["img_plate"] as? String
                    var img_plate_cut: String? = currentDoc["img_plate_cut"] as? String
                    var time_create: String? = currentDoc["time_create"] as? String
                    var expiration_date: String? = currentDoc["expiration_date"] as? String

                    val plateNumberObject = PlateNumberObject(
                        id_user = id_user ?: "",
                        infoPlate = info_plate ?: "",
                        region = region ?: "",
                        typeCar = type_car ?: "",
                        pay = pay ?: "",
                        cost = cost ?: 0,
                        accuracy = accuracy?.toFloatOrNull() ?: 0.0f,
                        imgPlate = img_plate ?: "",
                        imgPlateCut = img_plate_cut ?: "",
                        dateCreate = time_create ?: "",
                        expirationDate = expiration_date ?: ""
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
    fun loadDataSearch(id_user: String, querySearch: String) {
        app = App(AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")
        val query = Document("id_user",id_user).append("info_plate", querySearch)
        val findAllTask = mongoCollection.find(query).sort(Document("time_create", -1)).iterator()
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
                    var cost: Int? = currentDoc["cost"] as? Int
                    var accuracy: String? = currentDoc["accuracy"] as? String
                    var img_plate: String? = currentDoc["img_plate"] as? String
                    var img_plate_cut: String? = currentDoc["img_plate_cut"] as? String
                    var time_create: String? = currentDoc["time_create"] as? String
                    var expiration_date: String? = currentDoc["expiration_date"] as? String

                    val plateNumberObject = PlateNumberObject(
                        id_user = id_user ?: "",
                        infoPlate = info_plate ?: "",
                        region = region ?: "",
                        typeCar = type_car ?: "",
                        pay = pay ?: "",
                        cost = cost ?: 0,
                        accuracy = accuracy?.toFloatOrNull() ?: 0.0f,
                        imgPlate = img_plate ?: "",
                        imgPlateCut = img_plate_cut ?: "",
                        dateCreate = time_create ?: "",
                        expirationDate = expiration_date ?: ""
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
    fun loadDataFilter(id_user: String, queryFilter: String) {
        app = App(AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")
        val query = Document("id_user",id_user).append("pay", queryFilter)
        val findAllTask = mongoCollection.find(query).sort(Document("time_create", -1)).iterator()
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
                    var cost: Int? = currentDoc["cost"] as? Int
                    var accuracy: String? = currentDoc["accuracy"] as? String
                    var img_plate: String? = currentDoc["img_plate"] as? String
                    var img_plate_cut: String? = currentDoc["img_plate_cut"] as? String
                    var time_create: String? = currentDoc["time_create"] as? String
                    var expiration_date: String? = currentDoc["expiration_date"] as? String

                    val plateNumberObject = PlateNumberObject(
                        id_user = id_user ?: "",
                        infoPlate = info_plate ?: "",
                        region = region ?: "",
                        typeCar = type_car ?: "",
                        pay = pay ?: "",
                        cost = cost ?: 0,
                        accuracy = accuracy?.toFloatOrNull() ?: 0.0f,
                        imgPlate = img_plate ?: "",
                        imgPlateCut = img_plate_cut ?: "",
                        dateCreate = time_create ?: "",
                        expirationDate = expiration_date ?: ""
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
//                Toast.makeText(context, "Result: " + result.contents, Toast.LENGTH_LONG).show()
                Log.d("TAG_Y", "quetQR: "+result.contents)
                val bundle = Bundle()
                bundle.putString(
                    "key",
                    result.contents
                )
                view?.findNavController()?.navigate(R.id.action_main_fragment_to_blankFragment, bundle)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlateLibAdapter()
        binding?.apply {
            btnAdd.setOnClickListener {
                AnimationHelper.scaleAnimation(it, object : VoidCallback {
                    override fun execute() {
                        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_card_month, null)
                        val editText = dialogView.findViewById<EditText>(R.id.edt_plate_number)
                        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

                        val dialogBuilder = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setTitle("Chọn thời gian")
                        dialogBuilder.setPositiveButton("OK") { dialog, which ->
                            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                            val selectedRadioButton = dialogView.findViewById<RadioButton>(selectedRadioButtonId)
                            val selectedPlate = editText.text.toString()
                            val selectedOption = selectedRadioButton.text.toString()

                            // Xử lý dữ liệu đã chọn
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val currentDateTime = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                                val oneMonthLater = currentDateTime.plusMonths(1)
                                val threeMonthsLater = currentDateTime.plusMonths(3)
                                val sixMonthsLater = currentDateTime.plusMonths(6)

                                val formattedOneMonthLater = oneMonthLater.format(formatter)
                                val formattedThreeMonthsLater = threeMonthsLater.format(formatter)
                                val formattedSixMonthsLater = sixMonthsLater.format(formatter)
                                var timeExpire = ""

                                var price = 0
                                var monthRegister = 0
                                if(selectedOption == "1 tháng") {
                                    timeExpire = formattedOneMonthLater
                                    monthRegister = 1
                                }
                                if(selectedOption == "3 tháng") {
                                    timeExpire = formattedThreeMonthsLater
                                    monthRegister = 3
                                }
                                if(selectedOption == "6 tháng") {
                                    timeExpire = formattedSixMonthsLater
                                    monthRegister = 6
                                }

                                app = App(AppConfiguration.Builder(Appid).build())
                                val user = app.currentUser()
                                val queryFilter = Document("id_user", preferenceHelper.current_account_email ).append("info_plate", selectedPlate.lowercase())
                                if (user != null) {
                                    mongoClient = user.getMongoClient("mongodb-atlas")
                                }
                                mongoDatabase = mongoClient.getDatabase("number-plates-data")
                                val mongoCollection = mongoDatabase.getCollection("infoPlate")

                                val findTask: RealmResultTask<MongoCursor<Document>> =
                                    mongoCollection.find(queryFilter).iterator()
                                findTask.getAsync { task ->
                                    if (task.isSuccess) {
                                        val results: MongoCursor<Document> = task.get()
                                        if (results.hasNext()) {
                                            while (results.hasNext()) {
                                                val currentDoc: Document = results.next()
                                                var plateNumber: String? = currentDoc["info_plate"] as? String
                                                var idUser: String? = currentDoc["id_user"] as? String
                                                var expirationDate: String? = currentDoc["expiration_date"] as? String
                                                var typeVehical: String? = currentDoc["type_car"] as? String
                                                if(typeVehical == "car"){
                                                    price = monthRegister * preferenceHelper.price_car_month
                                                } else {
                                                    price = monthRegister * preferenceHelper.price_motorbike_month
                                                }
                                                if(price == 0){
                                                    Toast.makeText(this@LibraryFragment.requireContext(), "Bạn cần đặt giá vé trong mục thống kê", Toast.LENGTH_SHORT).show()
                                                    break
                                                }
                                                Log.d("TAG_YY",expirationDate+plateNumber+idUser+price )
                                                if(plateNumber != null && expirationDate == ""){
                                                    val updateFilter = Document("info_plate", plateNumber).append("id_user",idUser )
                                                    val updateDocument = Document("\$set", Document("expiration_date", timeExpire).append("pay", "true").append("cost",price ))
                                                    mongoCollection.updateOne(updateFilter, updateDocument).getAsync { task ->
                                                        if (task.isSuccess) {
                                                            Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                                                            loadData(preferenceHelper.current_account_email)
                                                        } else {
                                                            Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                                                            Log.v("Update Error", task.error.toString())
                                                        }
                                                    }
                                                } else if(plateNumber != null && expirationDate != ""){
                                                    Toast.makeText(requireContext(), "Vé xe biển số vừa nhập vẫn còn thời hạn", Toast.LENGTH_SHORT).show()

                                                }
                                            }
                                        }
                                    } else {
                                        Log.v("Task Error", task.error.toString())
                                    }
                                }
                            }
                        }

                        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }

                        val dialog = dialogBuilder.create()
                        dialog.show()
                    }
                }, 0.98f)
            }
            btnScan.setOnClickListener {
                AnimationHelper.scaleAnimation(it, object : VoidCallback {
                    override fun execute() {
                        val intentIntegrator = IntentIntegrator.forSupportFragment(this@LibraryFragment)
                        intentIntegrator.captureActivity = ScanQRActivity::class.java
                        intentIntegrator.setBeepEnabled(true)
                        intentIntegrator.setCameraId(0)
                        intentIntegrator.setPrompt("SCAN")
                        intentIntegrator.setBarcodeImageEnabled(false)
                        intentIntegrator.setOrientationLocked(true)
                        intentIntegrator.initiateScan()
                    }
                }, 0.98f)

            }
            binding?.rcvLib?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // Kiểm tra nếu người dùng cuộn lên
                    if (dy > 0) {
                        // Ẩn NavigationBottom
                       scrollUp()
                    } else {
                        // Hiển thị lại NavigationBottom
                        scrollDown()
                    }
                }
            })
            binding?.btnSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // Xử lý khi người dùng nhấn Enter hoặc nút tìm kiếm trên bàn phím
                    loadDataSearch(preferenceHelper.current_account_email, query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    loadDataSearch(preferenceHelper.current_account_email, newText)
                    return true
                }
            })
            binding?.btnSearch?.setOnCloseListener {
                // Xử lý sự kiện ở đây
                // Ví dụ: Clear dữ liệu tìm kiếm và hiển thị lại toàn bộ danh sách
                loadData(preferenceHelper.current_account_email)

                // Trả về true để ngăn việc đóng SearchView
                false
            }
            binding?.btnFilter?.setOnClickListener {
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter_lib, null)
                val radioAll = dialogView.findViewById<RadioButton>(R.id.radioAll)
                val radioPaid = dialogView.findViewById<RadioButton>(R.id.radioPaid)
                val radioUnpaid = dialogView.findViewById<RadioButton>(R.id.radioUnpaid)
                when (preferenceHelper.state_filter_lib) {
                    "All" -> radioAll.isChecked = true
                    "Paid" -> radioPaid.isChecked = true
                    "Unpaid" -> radioUnpaid.isChecked = true
                }

                val dialogBuilder = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setTitle("Lựa chọn hiển thị danh sách biển số")
                    .setPositiveButton("OK") { dialog, _ ->
                        // Xử lý sự kiện khi người dùng nhấn nút OK

                        when {
                            radioAll.isChecked -> {
                                loadData(preferenceHelper.current_account_email)
                                preferenceHelper.state_filter_lib = "All"
                            }
                            radioPaid.isChecked -> {
                                loadDataFilter(preferenceHelper.current_account_email, "true")
                                preferenceHelper.state_filter_lib = "Paid"
                            }
                            radioUnpaid.isChecked -> {
                                loadDataFilter(preferenceHelper.current_account_email, "false")
                                preferenceHelper.state_filter_lib = "Unpaid"
                            }
                        }

                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                val dialog = dialogBuilder.create()
                dialog.show()
            }

            // Set LayoutManager

            rcvLib.layoutManager = LinearLayoutManager(this@LibraryFragment.requireContext())

            // Set Adapter
            rcvLib.adapter = adapter

            // Sample data
            when (preferenceHelper.state_filter_lib) {
                "All" -> loadData(preferenceHelper.current_account_email)
                "Paid" -> loadDataFilter(preferenceHelper.current_account_email, "true")
                "Unpaid" -> loadDataFilter(preferenceHelper.current_account_email, "false")
            }

        }
    }
    private fun scrollUp(){
        homeListener?.scrollUp()
    }
    private fun scrollDown(){
        homeListener?.scrollDown()
    }
}