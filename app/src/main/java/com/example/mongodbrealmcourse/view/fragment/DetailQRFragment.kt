package com.example.mongodbrealmcourse.view.fragment

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.mongodbrealmcourse.databinding.FragmentDetailQRBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlin.experimental.xor


class DetailQRFragment : Fragment() {
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
            val value = receivedBundle.getString("key") // Thay "key" bằng key bạn đã sử dụng để đặt dữ liệu trong Bundle
            // Sử dụng dữ liệu nhận được ở đây
            value?.let {
                binding.resultText.text = encrypt("Ve??\u0002??;\u007F\n" +
                        "P???*??*<?\u000F??9??i\n" +
                        "\u0018??", "PLATEDETECH")
//                binding.resultText.text = encrypt("Ve??\u0002??;\u007F\n" +
//                        "P???*??*<?\u000F??9??i\n" +
//                        "\u0018??", "PLATEDETECH")
                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap =
                        barcodeEncoder.encodeBitmap(value, BarcodeFormat.QR_CODE, 400, 400)
                    binding.ivQr.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.d("TAG_A", e.toString())
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
}