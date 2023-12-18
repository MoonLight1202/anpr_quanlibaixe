package com.example.mongodbrealmcourse.view.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                                var timeCreate: String? = currentDoc["time_create"] as? String

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
                                    binding.tvInfoPlate.text = "Biển số: ${plateNumberObject.infoPlate.uppercase()}"
                                    if (plateNumberObject.typeCar == "car") {
                                        binding.tvTypeVehical.text = "Loại xe: Ô tô"
                                    } else binding.tvTypeVehical.text = "Loại xe: Xe máy"

                                    if (plateNumberObject.pay == "false") {
                                        binding.tvPay.text = "Chưa thanh toán"
                                        binding.tvPay.setTextColor(Color.RED)
                                    } else {
                                        binding.tvPay.text = "Đã thanh toán"
                                        binding.tvPay.setTextColor(Color.GREEN)
                                    }
                                    binding.tvTimeCreate.text = "Thời gian gửi: ${plateNumberObject.dateCreate}"
                                    binding.tvPoint.text =
                                        "Độ chính xác nhận diện: ${plateNumberObject.accuracy.toString()}"
                                    val mapper = ProvinceCodeMapper()
                                    val extractedCode =
                                        extractNumberBeforeLetter(plateNumberObject.infoPlate.uppercase()).substring(
                                            0,
                                            2
                                        ) // Lấy mã tỉnh thành từ chuỗi đầu vào
                                    val provinceName = mapper.getProvinceNameFromCode(extractedCode)
                                    binding.tvProvince.text = "Tỉnh thành: " + provinceName
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
                                    if (hours < 3) {
                                        if(expiration_date != ""){
                                            binding.tvTimeParking.text = "Thời gian gửi vé tháng đến: $expiration_date"
                                            binding.tvPrice.text = "Giá vé: ${cost}"
                                        } else {
                                            binding.tvTimeParking.text = "Thời gian đã gửi: $hours giờ"
                                            val price: Long
                                            if (plateNumberObject.typeCar == "car") {
                                                price = preferenceHelper.price_car_hour.toLong()
                                            } else {
                                                price = preferenceHelper.price_motorbike_hour.toLong()
                                            }
                                            binding.tvPrice.text = "Giá vé: ${price}"
                                        }

                                    } else {
                                          val days = calculateDays(hours)
                                          if(expiration_date != ""){
                                                binding.tvTimeParking.text = "Thời gian gửi vé tháng đến: $expiration_date"
                                                binding.tvPrice.text = "Giá vé: ${cost}"
                                          } else {
                                                binding.tvTimeParking.text = "Thời gian đã gửi: $days ngày"
                                                val price: Long
                                                if (plateNumberObject.typeCar == "car") {
                                                    price = days*preferenceHelper.price_car_day.toLong()
                                                } else {
                                                    price = days*preferenceHelper.price_motorbike_day.toLong()
                                                }
                                                binding.tvPrice.text = "Giá vé: ${price}"
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
                    if (receivedObject.imgPlate != "") {
                        Glide.with(this)
                            .load(receivedObject.imgPlate)
                            .into(binding.ivPlateFull)
                    } else {
                        binding.ivPlateFull.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.bienso
                            )
                        )
                    }
                    binding.tvInfoPlate.text = "Biển số: ${receivedObject.infoPlate.uppercase()}"
                    if (receivedObject.typeCar == "car") {
                        binding.tvTypeVehical.text = "Loại xe: Ô tô"
                    } else binding.tvTypeVehical.text = "Loại xe: Xe máy"

                    if (receivedObject.pay == "false") {
                        binding.tvPay.text = "Chưa thanh toán"
                        binding.tvPay.setTextColor(Color.RED)
                    } else {
                        binding.tvPay.text = "Đã thanh toán"
                        binding.tvPay.setTextColor(Color.GREEN)

                    }
                    binding.tvTimeCreate.text = "Thời gian gửi: ${receivedObject.dateCreate}"
                    binding.tvPoint.text =
                        "Độ chính xác nhận diện: ${receivedObject.accuracy.toString()}"
                    val mapper = ProvinceCodeMapper()
                    val extractedCode =
                        extractNumberBeforeLetter(receivedObject.infoPlate.uppercase()).substring(
                            0,
                            2
                        ) // Lấy mã tỉnh thành từ chuỗi đầu vào
                    val provinceName = mapper.getProvinceNameFromCode(extractedCode)
                    binding.tvProvince.text = "Tỉnh thành: " + provinceName
                    try {
                        val barcodeEncoder = BarcodeEncoder()
                        val info =
                            "CS12 Plate Number:$" + receivedObject.infoPlate + "$" + "Time:$" + receivedObject.dateCreate + "$" + "Type Vehical:$" + receivedObject.typeCar + "$"
                        val bitmap =
                            barcodeEncoder.encodeBitmap(info, BarcodeFormat.QR_CODE, 400, 400)
                        binding.ivQr.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        Log.d("TAG_A", e.toString())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                        val currentTime = LocalDateTime.now()
                        val createTime = LocalDateTime.parse(receivedObject.dateCreate, formatter)
                        val duration = Duration.between(createTime, currentTime)
                        val hours = duration.toHours()
                        Log.d("TAG_Y", "$hours giờ")
                        if (hours < 3) {
                            if(receivedObject.expirationDate != ""){
                                binding.tvTimeParking.text = "Thời gian gửi vé tháng đến: ${receivedObject.expirationDate}"
                                binding.tvPrice.text = "Giá vé: ${receivedObject.cost}"
                            } else {
                                binding.tvTimeParking.text = "Thời gian gửi: $hours giờ"
                                val price: Long
                                if (receivedObject.typeCar == "car") {
                                    price = preferenceHelper.price_car_hour.toLong()
                                } else {
                                    price = preferenceHelper.price_motorbike_hour.toLong()
                                }
                                binding.tvPrice.text = "Giá vé: ${price}"
                            }
                        } else {
                            val days = calculateDays(hours)
                            if(receivedObject.expirationDate != ""){
                                binding.tvTimeParking.text = "Thời gian gửi vé tháng đến: ${receivedObject.expirationDate}"
                                binding.tvPrice.text = "Giá vé: ${receivedObject.cost}"
                            } else {
                                binding.tvTimeParking.text = "Thời gian gửi: $days ngày"
                                val price: Long
                                if (receivedObject.typeCar == "car") {
                                    price = days*preferenceHelper.price_car_day.toLong()
                                } else {
                                    price = days*preferenceHelper.price_motorbike_day.toLong()
                                }
                                binding.tvPrice.text = "Giá vé: ${price}"
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
