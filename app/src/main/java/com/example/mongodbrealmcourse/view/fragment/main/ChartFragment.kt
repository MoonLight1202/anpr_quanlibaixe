package com.example.mongodbrealmcourse.view.fragment.main

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentChartBinding
import com.example.mongodbrealmcourse.model.`object`.PlateNumberObject
import com.example.mongodbrealmcourse.view.fragment.BaseFragment
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.example.mongodbrealmcourse.viewmodel.utils.PreferenceHelper
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.mongo.iterable.MongoCursor
import org.bson.Document
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class ChartFragment : BaseFragment() {
    private lateinit var binding: FragmentChartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            root.setOnTouchListener { _, _ ->
                hideKeyboard()
                false
            }
            addTextChangedListenerForEditText(edtPriceMotorbikeHour, preferenceHelper, "motorbike_hour")
            addTextChangedListenerForEditText(edtPriceMotorbikeDay, preferenceHelper, "motorbike_day")
            addTextChangedListenerForEditText(edtPriceMotorbikeMonth, preferenceHelper, "motorbike_month")
            addTextChangedListenerForEditText(edtPriceCarHour, preferenceHelper, "car_hour")
            addTextChangedListenerForEditText(edtPriceCarDay, preferenceHelper, "car_day")
            addTextChangedListenerForEditText(edtPriceCarMonth, preferenceHelper, "car_month")
            edtPriceMotorbikeHour.text = Editable.Factory.getInstance().newEditable(preferenceHelper.price_motorbike_hour.toString())
            edtPriceMotorbikeDay.text = Editable.Factory.getInstance().newEditable(preferenceHelper.price_motorbike_day.toString())
            edtPriceMotorbikeMonth.text = Editable.Factory.getInstance().newEditable(preferenceHelper.price_motorbike_month.toString())
            edtPriceCarHour.text = Editable.Factory.getInstance().newEditable(preferenceHelper.price_car_hour.toString())
            edtPriceCarDay.text = Editable.Factory.getInstance().newEditable(preferenceHelper.price_car_day.toString())
            edtPriceCarMonth.text = Editable.Factory.getInstance().newEditable(preferenceHelper.price_car_month.toString())
 //           val density = resources.displayMetrics.density
//            ivMon.layoutParams.height = (200 * density).toInt()
//            ivTue.layoutParams.height = (120 * density).toInt()
//            ivWed.layoutParams.height = (150 * density).toInt()
//            ivThu.layoutParams.height = (100 * density).toInt()
//            ivFri.layoutParams.height = (80 * density).toInt()
//            ivSat.layoutParams.height = (180 * density).toInt()
//            ivSun.layoutParams.height = (135 * density).toInt()
            loadDataForToday()
            chartToday.setOnClickListener {
                it.setBackgroundColor(ContextCompat.getColor(this@ChartFragment.requireContext(), R.color.purpil_4))
                chartWeek.background = null
                chartMonth.background = null
                chartCustom.background = null
                timeChart.text = getString(R.string.today)
                loadDataForToday()
            }
            chartWeek.setOnClickListener {
                it.setBackgroundColor(ContextCompat.getColor(this@ChartFragment.requireContext(), R.color.purpil_4))
                chartToday.background = null
                chartMonth.background = null
                chartCustom.background = null
                timeChart.text = getString(R.string.this_week)
                loadDataForWeek()
            }
            chartMonth.setOnClickListener {
                it.setBackgroundColor(ContextCompat.getColor(this@ChartFragment.requireContext(), R.color.purpil_4))
                chartWeek.background = null
                chartToday.background = null
                chartCustom.background = null
                timeChart.text = getString(R.string.this_month)
                loadDataForMonth()
            }
            chartCustom.setOnClickListener {
                it.setBackgroundColor(ContextCompat.getColor(this@ChartFragment.requireContext(), R.color.purpil_4))
                chartWeek.background = null
                chartMonth.background = null
                chartToday.background = null
                timeChart.text = getString(R.string.options)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val startDatePicker = DatePickerDialog(requireContext(), R.style.DatePickerDialogTheme)
                    startDatePicker.setOnDateSetListener { _, startYear, startMonth, startDay ->
                        val endDatePicker = DatePickerDialog(requireContext(), R.style.DatePickerDialogTheme)
                        endDatePicker.setOnDateSetListener { _, endYear, endMonth, endDay ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val startDate = LocalDate.of(startYear, startMonth + 1, startDay).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                val endDate = LocalDate.of(endYear, endMonth + 1, endDay).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                val startDateTime = LocalDateTime.parse("00:00:00 $startDate", DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"))
                                val endDateTime = LocalDateTime.parse("23:59:59 $endDate", DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"))
                                Log.d("TAG_YY", startDateTime.toString()+endDateTime)
                                timeChart.text = "$startDate - $endDate"
                                loadDataForCustom(startDateTime, endDateTime)
                            }
                        }
                        endDatePicker.show()
                    }
                    startDatePicker.show()
                }
            }
        }
    }
    fun loadDataForToday() {
        var countCar = 0
        var countMotor = 0
        var countCarPaid = 0
        var countMotorPaid = 0
        var doanhthuMotor = 0
        var doanhthuCar = 0
        app = App (AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")

        // Lấy ngày hôm nay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
            Log.d("TAG_YY", today)

            // Xây dựng truy vấn để tìm các bản ghi với phần ngày của time_create là ngày hôm nay
            val query = Document("id_user", preferenceHelper.current_account_email)
                .append("time_create", Document("\$regex", today))

            val findAllTask = mongoCollection.find(query).sort(Document("time_create", -1)).iterator()
            val plateNumberList = mutableListOf<PlateNumberObject>()

            findAllTask.getAsync { task ->
                if (task.isSuccess) {
                    val results: MongoCursor<Document> = task.get()
                    Log.d("TAG_YY", plateNumberList.size.toString())

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

                        if(plateNumberObject.typeCar == "car") {
                            countCar++
                            doanhthuCar+=plateNumberObject.cost
                            if(plateNumberObject.pay == "true") countCarPaid++
                        } else {
                            countMotor++
                            doanhthuMotor+=plateNumberObject.cost
                            if(plateNumberObject.pay == "true") countMotorPaid++
                        }
                    }
                    binding.tvCountCar.text = getString(R.string.car_count)+ " $countCar"
                    binding.tvCountMotor.text = getString(R.string.motorcycle_count)+ " $countMotor"
                    binding.tvCountCarPaid.text = getString(R.string.paid)+ ": $countCarPaid"
                    binding.tvCountMotorPaid.text = getString(R.string.paid)+ ": $countMotorPaid"
                    binding.tvDoanhthuMotor.text = getString(R.string.motorcycle_revenue)+ " $doanhthuMotor"
                    binding.tvDoanhthuCar.text = getString(R.string.car_revenue)+ " $doanhthuCar"
                    binding.tvAllDoanhThu.text = getString(R.string.total_revenue)+ " ${doanhthuMotor+doanhthuCar}"

                } else {
                    Log.v("TAG_YY", task.error.toString())
                }
            }
        }
    }

    fun loadDataForWeek() {
        var countCar = 0
        var countMotor = 0
        var countCarPaid = 0
        var countMotorPaid = 0
        var doanhthuMotor = 0
        var doanhthuCar = 0
        app = App(AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val query = Document("id_user", preferenceHelper.current_account_email)


            val findAllTask = mongoCollection.find(query).iterator()

            findAllTask.getAsync { task ->
                if (task.isSuccess) {
                    val results: MongoCursor<Document> = task.get()

                    if (!results.hasNext()) {
                        Toast.makeText(
                            this@ChartFragment.requireContext(),
                            getString(R.string.no_elements),
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
                        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                        val dayDate = LocalDateTime.parse(plateNumberObject.dateCreate, formatter)
                        val startDate =
                            LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                .atTime(LocalTime.MIN)
                        val endDate =
                            LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                                .atTime(LocalTime.MAX)
                        Log.d("TAG_YY", startDate.toString() + "--" + dayDate + "--" + endDate)
                        if (dayDate.isAfter(startDate) && dayDate.isBefore(endDate)) {
                            if (plateNumberObject.typeCar == "car") {
                                countCar++
                                doanhthuCar += plateNumberObject.cost
                                if (plateNumberObject.pay == "true") countCarPaid++
                            } else {
                                countMotor++
                                doanhthuMotor += plateNumberObject.cost
                                if (plateNumberObject.pay == "true") countMotorPaid++
                            }
                        }
                    }
                    binding.tvCountCar.text = getString(R.string.car_count)+ " $countCar"
                    binding.tvCountMotor.text = getString(R.string.motorcycle_count)+ " $countMotor"
                    binding.tvCountCarPaid.text = getString(R.string.paid)+ ": $countCarPaid"
                    binding.tvCountMotorPaid.text = getString(R.string.paid)+ ": $countMotorPaid"
                    binding.tvDoanhthuMotor.text = getString(R.string.motorcycle_revenue)+ " $doanhthuMotor"
                    binding.tvDoanhthuCar.text = getString(R.string.car_revenue)+ " $doanhthuCar"
                    binding.tvAllDoanhThu.text = getString(R.string.total_revenue)+ " ${doanhthuMotor+doanhthuCar}"
                    } else {
                    Log.v("TAG_YY", task.error.toString())
                }
            }
        }
    }
    fun loadDataForMonth() {
        var countCar = 0
        var countMotor = 0
        var countCarPaid = 0
        var countMotorPaid = 0
        var doanhthuMotor = 0
        var doanhthuCar = 0
        app = App(AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val query = Document("id_user", preferenceHelper.current_account_email)


            val findAllTask = mongoCollection.find(query).iterator()

            findAllTask.getAsync { task ->
                if (task.isSuccess) {
                    val results: MongoCursor<Document> = task.get()

                    if (!results.hasNext()) {
                        Toast.makeText(
                            this@ChartFragment.requireContext(),
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
                        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                        val dayDate = LocalDateTime.parse(plateNumberObject.dateCreate, formatter)
                        val startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
                            .atTime(LocalTime.MIN)
                        val endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
                            .atTime(LocalTime.MAX)
                        Log.d("TAG_YY", startDate.toString() + "--" + dayDate + "--" + endDate)
                        if (dayDate.isAfter(startDate) && dayDate.isBefore(endDate)) {
                            if (plateNumberObject.typeCar == "car") {
                                countCar++
                                doanhthuCar += plateNumberObject.cost
                                if (plateNumberObject.pay == "true") countCarPaid++
                            } else {
                                countMotor++
                                doanhthuMotor += plateNumberObject.cost
                                if (plateNumberObject.pay == "true") countMotorPaid++
                            }
                        }
                        binding.tvCountCar.text = getString(R.string.car_count)+ " $countCar"
                        binding.tvCountMotor.text = getString(R.string.motorcycle_count)+ " $countMotor"
                        binding.tvCountCarPaid.text = getString(R.string.paid)+ ": $countCarPaid"
                        binding.tvCountMotorPaid.text = getString(R.string.paid)+ ": $countMotorPaid"
                        binding.tvDoanhthuMotor.text = getString(R.string.motorcycle_revenue)+ " $doanhthuMotor"
                        binding.tvDoanhthuCar.text = getString(R.string.car_revenue)+ " $doanhthuCar"
                        binding.tvAllDoanhThu.text = getString(R.string.total_revenue)+ " ${doanhthuMotor+doanhthuCar}"

                    }
                } else {
                    Log.v("TAG_YY", task.error.toString())
                }
            }
        }
    }
    fun loadDataForCustom(start: LocalDateTime, end: LocalDateTime) {
        var countCar = 0
        var countMotor = 0
        var countCarPaid = 0
        var countMotorPaid = 0
        var doanhthuMotor = 0
        var doanhthuCar = 0
        app = App(AppConfiguration.Builder(Appid).build())

        val user = app.currentUser()
        if (user != null) {
            mongoClient = user.getMongoClient("mongodb-atlas")
        }
        mongoDatabase = mongoClient.getDatabase("number-plates-data")
        val mongoCollection = mongoDatabase.getCollection("infoPlate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val query = Document("id_user", preferenceHelper.current_account_email)


            val findAllTask = mongoCollection.find(query).iterator()

            findAllTask.getAsync { task ->
                if (task.isSuccess) {
                    val results: MongoCursor<Document> = task.get()

                    if (!results.hasNext()) {
                        Toast.makeText(
                            this@ChartFragment.requireContext(),
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
                        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                        val dayDate = LocalDateTime.parse(plateNumberObject.dateCreate, formatter)
                        if (dayDate.isAfter(start) && dayDate.isBefore(end)) {
                            Log.d("TAG_YY", dayDate.toString())
                            if (plateNumberObject.typeCar == "car") {
                                countCar++
                                doanhthuCar += plateNumberObject.cost
                                if (plateNumberObject.pay == "true") countCarPaid++
                            } else {
                                countMotor++
                                doanhthuMotor += plateNumberObject.cost
                                if (plateNumberObject.pay == "true") countMotorPaid++
                            }
                        }
                        binding.tvCountCar.text = getString(R.string.car_count)+ " $countCar"
                        binding.tvCountMotor.text = getString(R.string.motorcycle_count)+ " $countMotor"
                        binding.tvCountCarPaid.text = getString(R.string.paid)+ ": $countCarPaid"
                        binding.tvCountMotorPaid.text = getString(R.string.paid)+ ": $countMotorPaid"
                        binding.tvDoanhthuMotor.text = getString(R.string.motorcycle_revenue)+ " $doanhthuMotor"
                        binding.tvDoanhthuCar.text = getString(R.string.car_revenue)+ " $doanhthuCar"
                        binding.tvAllDoanhThu.text = getString(R.string.total_revenue)+ " ${doanhthuMotor+doanhthuCar}"
                    }
                } else {
                    Log.v("TAG_YY", task.error.toString())
                }
            }
        }
    }
    fun addTextChangedListenerForEditText(
        editText: EditText,
        preferenceHelper: PreferenceHelper,
        priceType: String
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Được gọi trước khi văn bản trong EditText thay đổi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Được gọi khi văn bản trong EditText thay đổi
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    when (priceType) {
                        "motorbike_hour" -> preferenceHelper.price_motorbike_hour = s.toString().toInt()
                        "motorbike_day" -> preferenceHelper.price_motorbike_day = s.toString().toInt()
                        "motorbike_month" -> preferenceHelper.price_motorbike_month = s.toString().toInt()
                        "car_hour" -> preferenceHelper.price_car_hour = s.toString().toInt()
                        "car_day" -> preferenceHelper.price_car_day = s.toString().toInt()
                        "car_month" -> preferenceHelper.price_car_month = s.toString().toInt()

                        // Thêm các trường hợp khác nếu cần
                    }
                }
            }
        })
    }
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
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