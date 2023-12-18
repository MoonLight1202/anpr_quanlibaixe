package com.example.mongodbrealmcourse.view.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mongodbrealmcourse.databinding.FragmentDetailQRBinding
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import org.bson.Document
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.experimental.xor


class DetailQRFragment : BaseFragment() {
    private lateinit var binding: FragmentDetailQRBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailQRBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        if (receivedBundle != null) {
            val value = receivedBundle.getString("key")
            // Sử dụng dữ liệu nhận được ở đây
            value?.let {
                val plateNumber = value.substringAfter("Plate Number:$").substringBefore("$")
                val time = value.substringAfter("Time:$").substringBefore("$")
                Log.d("TAG_Y", plateNumber+time)

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
                                // Calculate time difference
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                                    val currentTime = LocalDateTime.now()
                                    val createTime = LocalDateTime.parse(timeCreate, formatter)
                                    val duration = Duration.between(createTime, currentTime)
                                    val hours = duration.toHours()
                                    Log.d("TAG_Y", "$hours giờ")
                                    if(hours < 3){
                                        Log.d("TAG_Y", "$hours hour(s)")
                                    } else {
                                        val days = calculateDays(hours)
                                        Log.d("TAG_Y", "$days day(s)")
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
    fun encrypt(text: String, key: String): String {
        val textBytes = text.toByteArray(Charsets.UTF_8)
        val keyBytes = key.toByteArray(Charsets.UTF_8)

        for (i in textBytes.indices) {
            textBytes[i] = textBytes[i] xor keyBytes[i % keyBytes.size]
        }

        return android.util.Base64.encodeToString(textBytes, android.util.Base64.DEFAULT)
    }
    fun calculateDays(hours: Long): Long {
        var days = 0L

        if (hours > 24) {
            days = hours / 24

            if (hours > 48) {
                days += 1
            }
        }
        return days
    }
}