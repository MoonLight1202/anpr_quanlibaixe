package com.example.mongodbrealmcourse.view.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentBlankBinding
import com.example.mongodbrealmcourse.model.`object`.PlateNumberObject
import com.example.mongodbrealmcourse.viewmodel.utils.ProvinceCodeMapper
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import org.bson.Document
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BlankFragment : BaseFragment() {

    private lateinit var binding: FragmentBlankBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        val receivedObject: PlateNumberObject? = receivedBundle?.getParcelable("plateObject")
        if (receivedBundle != null) {
            val receiveQR = receivedBundle.getString("key")
            if (receiveQR != null) {
                binding.layoutNoResult.visibility = View.GONE
                binding.layoutResultPlate.visibility = View.VISIBLE
                val plateNumber = receiveQR.substringAfter("Plate Number:$").substringBefore("$")
                val time = receiveQR.substringAfter("Time:$").substringBefore("$")
                Log.d("TAG_Y", plateNumber + time)

                // Truy vấn theo `infoplate` và `time_create`
                app = App(AppConfiguration.Builder(Appid).build())
                val user = app.currentUser()
                if (user != null) {
                    mongoClient = user.getMongoClient("mongodb-atlas")
                    mongoDatabase = mongoClient.getDatabase("number-plates-data")
                    val mongoCollection = mongoDatabase.getCollection("infoPlate")
                    val query = Document("info_plate", plateNumber).append("time_create", time)
                    // Tìm các document phù hợp với truy vấn
                    val result = mongoCollection?.findOne(query)
                    // Kiểm tra kết quả
                    if (result != null) {
                        result.getAsync { task ->
                            if (task.isSuccess) {
                                val currentDoc = task.get()
                                val timeCreate: String? = currentDoc["time_create"] as? String

                                val id_user: String? = currentDoc["id_user"] as? String
                                val info_plate: String? = currentDoc["info_plate"] as? String
                                val region: String? = currentDoc["region"] as? String
                                val type_car: String? = currentDoc["type_car"] as? String
                                val pay: String? = currentDoc["pay"] as? String
                                val cost: Int? = currentDoc["cost"] as? Int
                                val accuracy: String? = currentDoc["accuracy"] as? String
                                val img_plate: String? = currentDoc["img_plate"] as? String
                                val img_plate_cut: String? = currentDoc["img_plate_cut"] as? String
                                val time_create: String? = currentDoc["time_create"] as? String
                                val expiration_date: String? = currentDoc["expiration_date"] as? String

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

                                // Calculate time difference
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val formatter =
                                        DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                                    val currentTime = LocalDateTime.now()
                                    val createTime = LocalDateTime.parse(timeCreate, formatter)
                                    val duration = Duration.between(createTime, currentTime)
                                    val hours = duration.toHours()
                                    if (plateNumberObject.imgPlate != "") {
                                        Glide.with(this)
                                            .load(plateNumberObject.imgPlate)
                                            .into(binding.ivPlateFull)
                                    } else {
                                        binding.ivPlateFull.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                binding.root.context,
                                                R.drawable.bienso
                                            )
                                        )
                                    }
                                    binding.tvInfoPlate.text = getString(R.string.license_plate) +" ${plateNumberObject.infoPlate.uppercase()}"
                                    if (plateNumberObject.typeCar == "car") {
                                        binding.tvTypeVehical.text = getString(R.string.vehicle_type) +" "+ getString(R.string.car)
                                    } else binding.tvTypeVehical.text = getString(R.string.vehicle_type) +" "+ getString(R.string.motorcycle)

                                    binding.tvTimeCreate.text = getString(R.string.entry_time)+" ${plateNumberObject.dateCreate}"
                                    binding.tvPoint.text =
                                        getString(R.string.accuracy)+" ${plateNumberObject.accuracy.toString()}"
                                    val mapper = ProvinceCodeMapper()
                                    try {
                                        val extractedCode =
                                            extractNumberBeforeLetter(plateNumberObject.infoPlate.uppercase()).substring(
                                                0,
                                                2
                                            ) // Lấy mã tỉnh thành từ chuỗi đầu vào
                                        val provinceName = mapper.getProvinceNameFromCode(extractedCode)
                                        binding.tvProvince.text = getString(R.string.province) +" "+ provinceName
                                    } catch (e: java.lang.Exception){
                                        binding.tvProvince.text = getString(R.string.province) +" Không xác định"
                                    }
                                    binding.ivQr.visibility = View.GONE

                                    if (hours < 3) {
                                        if(expiration_date != ""){
                                            binding.tvTimeParking.text = getString(R.string.monthly_ticket_sent_until)+" $expiration_date"
                                            binding.tvPrice.text = getString(R.string.ticket_price)+" ${cost}"
                                        } else {
                                            binding.tvTimeParking.text = getString(R.string.time_sent)+" $hours "+getString(R.string.hour)
                                            val price: Long
                                            if (plateNumberObject.typeCar == "car") {
                                                price = preferenceHelper.price_car_hour.toLong()
                                            } else {
                                                price = preferenceHelper.price_motorbike_hour.toLong()
                                            }
                                            binding.tvPrice.text = getString(R.string.ticket_price)+" $price"

                                            val updateDocument = Document("\$set", Document("pay", "true").append("cost", price.toInt()))

                                            mongoCollection.updateOne(query, updateDocument).getAsync { task ->
                                                if (task.isSuccess) {
                                                    Toast.makeText(requireContext(), getString(R.string.pay_success), Toast.LENGTH_SHORT).show()
                                                    binding.tvPay.text = getString(R.string.paid)
                                                    binding.tvPay.setTextColor(Color.GREEN)
                                                } else {
                                                    Toast.makeText(requireContext(), getString(R.string.pay_failed), Toast.LENGTH_SHORT).show()
                                                    Log.v("Update Error", task.error.toString())
                                                    binding.tvPay.text = getString(R.string.unpaid)
                                                    binding.tvPay.setTextColor(Color.RED)
                                                }
                                            }
                                        }

                                    } else {
                                          val days = calculateDays(hours)
                                          if(expiration_date != ""){
                                              binding.tvTimeParking.text = getString(R.string.monthly_ticket_sent_until)+" $expiration_date"
                                              binding.tvPrice.text = getString(R.string.ticket_price)+" ${cost}"
                                          } else {
                                              binding.tvTimeParking.text = getString(R.string.time_sent)+" $days "+getString(R.string.day)
                                                val price: Long
                                                if (plateNumberObject.typeCar == "car") {
                                                    price = days*preferenceHelper.price_car_day.toLong()
                                                } else {
                                                    price = days*preferenceHelper.price_motorbike_day.toLong()
                                                }
                                              binding.tvPrice.text = getString(R.string.ticket_price)+" $price"
                                              val updateDocument = Document("\$set", Document("pay", "true").append("cost", price.toInt()))

                                              mongoCollection.updateOne(query, updateDocument).getAsync { task ->
                                                  if (task.isSuccess) {
                                                      Toast.makeText(requireContext(), getString(R.string.pay_success), Toast.LENGTH_SHORT).show()
                                                      binding.tvPay.text = getString(R.string.paid)
                                                      binding.tvPay.setTextColor(Color.GREEN)
                                                  } else {
                                                      Toast.makeText(requireContext(), getString(R.string.pay_failed), Toast.LENGTH_SHORT).show()
                                                      Log.v("Update Error", task.error.toString())
                                                      binding.tvPay.text = getString(R.string.unpaid)
                                                      binding.tvPay.setTextColor(Color.RED)
                                                  }
                                              }
                                          }
                                    }
                                }
                            } else {
                                Log.v("TAG_Y", task.error.toString())
                            }
                        }
                    }
                }
            } else {
                Log.d("TAG_Y", "null")
                if (receivedObject == null) {
                    binding.layoutNoResult.visibility = View.VISIBLE
                    binding.layoutResultPlate.visibility = View.GONE
                } else {
                    binding.layoutNoResult.visibility = View.GONE
                    binding.layoutResultPlate.visibility = View.VISIBLE
                    // Truy vấn theo `infoplate` và `time_create`
                    app = App(AppConfiguration.Builder(Appid).build())
                    val user = app.currentUser()
                    if (user != null) {
                        mongoClient = user.getMongoClient("mongodb-atlas")
                        mongoDatabase = mongoClient.getDatabase("number-plates-data")
                        val mongoCollection = mongoDatabase.getCollection("infoPlate")
                        val query = Document("info_plate", receivedObject.infoPlate).append("time_create", receivedObject.dateCreate)
                        // Tìm các document phù hợp với truy vấn
                        val result = mongoCollection?.findOne(query)
                        // Kiểm tra kết quả
                        if (result != null) {
                            result.getAsync { task ->
                                if (task.isSuccess) {
                                    val currentDoc = task.get()
                                    val timeCreate: String? = currentDoc["time_create"] as? String

                                    val id_user: String? = currentDoc["id_user"] as? String
                                    val info_plate: String? = currentDoc["info_plate"] as? String
                                    val region: String? = currentDoc["region"] as? String
                                    val type_car: String? = currentDoc["type_car"] as? String
                                    val pay: String? = currentDoc["pay"] as? String
                                    val cost: Int? = currentDoc["cost"] as? Int
                                    val accuracy: String? = currentDoc["accuracy"] as? String
                                    val img_plate: String? = currentDoc["img_plate"] as? String
                                    val img_plate_cut: String? = currentDoc["img_plate_cut"] as? String
                                    val time_create: String? = currentDoc["time_create"] as? String
                                    val expiration_date: String? = currentDoc["expiration_date"] as? String

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

                                    // Calculate time difference
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val formatter =
                                            DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                                        val currentTime = LocalDateTime.now()
                                        val createTime = LocalDateTime.parse(timeCreate, formatter)
                                        val duration = Duration.between(createTime, currentTime)
                                        val hours = duration.toHours()
                                        if (plateNumberObject.imgPlate != "") {
                                            Glide.with(this)
                                                .load(plateNumberObject.imgPlate)
                                                .into(binding.ivPlateFull)
                                        } else {
                                            binding.ivPlateFull.setImageDrawable(
                                                ContextCompat.getDrawable(
                                                    binding.root.context,
                                                    R.drawable.bienso
                                                )
                                            )
                                        }
                                        if (plateNumberObject.pay == "false") {
                                            binding.tvPay.text = getString(R.string.unpaid)
                                            binding.tvPay.setTextColor(Color.RED)
                                        } else {
                                            binding.tvPay.text = getString(R.string.paid)
                                            binding.tvPay.setTextColor(Color.GREEN)
                                        }
                                        binding.tvInfoPlate.text = getString(R.string.license_plate) +" ${plateNumberObject.infoPlate.uppercase()}"
                                        if (plateNumberObject.typeCar == "car") {
                                            binding.tvTypeVehical.text = getString(R.string.vehicle_type) +" "+ getString(R.string.car)
                                        } else binding.tvTypeVehical.text = getString(R.string.vehicle_type) +" "+ getString(R.string.motorcycle)

                                        binding.tvTimeCreate.text = getString(R.string.entry_time)+" ${plateNumberObject.dateCreate}"
                                        binding.tvPoint.text =
                                            getString(R.string.accuracy)+" ${plateNumberObject.accuracy.toString()}"
                                        val mapper = ProvinceCodeMapper()
                                        try {
                                            val extractedCode =
                                                extractNumberBeforeLetter(plateNumberObject.infoPlate.uppercase()).substring(
                                                    0,
                                                    2
                                                ) // Lấy mã tỉnh thành từ chuỗi đầu vào
                                            val provinceName = mapper.getProvinceNameFromCode(extractedCode)
                                            binding.tvProvince.text = getString(R.string.province) +" "+ provinceName
                                        } catch (e: java.lang.Exception){
                                            binding.tvProvince.text = getString(R.string.province) +" Không xác định"
                                        }
                                        if(plateNumberObject.pay=="true")  {
                                            binding.ivQr.visibility = View.GONE
                                        } else {
                                            binding.ivQr.visibility = View.VISIBLE
                                            try {
                                                val barcodeEncoder = BarcodeEncoder()
                                                val info =
                                                    "CS12 Plate Number:$" + plateNumberObject.infoPlate + "$" + "Time:$" + plateNumberObject.dateCreate + "$" + "Type Vehical:$" + plateNumberObject.typeCar + "$"
                                                val bitmap =
                                                    barcodeEncoder.encodeBitmap(info, BarcodeFormat.QR_CODE, 400, 400)
                                                binding.ivQr.setImageBitmap(bitmap)
                                            } catch (e: Exception) {
                                                Log.d("TAG_A", e.toString())
                                            }
                                        }
                                        if (hours < 3) {
                                            if(expiration_date != ""){
                                                binding.tvTimeParking.text = getString(R.string.monthly_ticket_sent_until)+" $expiration_date"
                                                binding.tvPrice.text = getString(R.string.ticket_price)+" ${cost}"
                                            } else {
                                                binding.tvTimeParking.text = getString(R.string.time_sent)+" $hours "+getString(R.string.hour)
                                                val price: Long
                                                if (plateNumberObject.typeCar == "car") {
                                                    price = preferenceHelper.price_car_hour.toLong()
                                                } else {
                                                    price = preferenceHelper.price_motorbike_hour.toLong()
                                                }
                                                Log.d("TAG_II",plateNumberObject.cost.toString() + "---"+ price)
                                                if(plateNumberObject.cost <= 1000) binding.tvPrice.text = getString(R.string.ticket_price)+ " ${price}" else binding.tvPrice.text = getString(R.string.ticket_price)+ " ${plateNumberObject.cost}"
                                                if(plateNumberObject.pay=="true" && plateNumberObject.expirationDate=="") binding.tvTimeParking.text = getString(R.string.time_sent)

                                            }

                                        } else {
                                            val days = calculateDays(hours)
                                            if(expiration_date != ""){
                                                binding.tvTimeParking.text = getString(R.string.monthly_ticket_sent_until)+" $expiration_date"
                                                binding.tvPrice.text = getString(R.string.ticket_price)+" ${cost}"
                                            } else {
                                                binding.tvTimeParking.text = getString(R.string.time_sent)+" $days "+getString(R.string.day)
                                                val price: Long
                                                if (plateNumberObject.typeCar == "car") {
                                                    price = days*preferenceHelper.price_car_day.toLong()
                                                } else {
                                                    price = days*preferenceHelper.price_motorbike_day.toLong()
                                                }
                                                Log.d("TAG_II",plateNumberObject.cost.toString() + "---"+ price)

                                                if(plateNumberObject.cost <= 1000) binding.tvPrice.text = getString(R.string.ticket_price)+ " ${price}" else binding.tvPrice.text = getString(R.string.ticket_price)+ " ${plateNumberObject.cost}"
                                                if(plateNumberObject.pay=="true" && plateNumberObject.expirationDate=="") binding.tvTimeParking.text = getString(R.string.time_sent)

                                            }
                                        }
                                    }
                                } else {
                                    Log.v("TAG_Y", task.error.toString())
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.layoutBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun extractNumberBeforeLetter(input: String): String {
        val regex = Regex("(\\d+)[A-Za-z]")
        val matchResult = regex.find(input)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    fun calculateDays(hours: Long): Long {
        var days = 1L

        if (hours > 24) {
            days = hours / 24

            if (hours > 48) {
                days += 1
            }
        }
        return days
    }
}
