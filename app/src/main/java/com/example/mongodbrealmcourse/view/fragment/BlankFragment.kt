package com.example.mongodbrealmcourse.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentBlankBinding
import com.example.mongodbrealmcourse.model.`object`.PlateNumberObject
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


class BlankFragment : Fragment(){

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
        val receivedObject: PlateNumberObject = receivedBundle?.getParcelable("plateObject")!!
        if(receivedObject.imgPlate != ""){
            Glide.with(this)
                .load(receivedObject.imgPlate)
                .into(binding.ivPlateFull)
        } else {
            binding.ivPlateFull.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.bienso))
        }
        binding.tvInfoPlate.text = receivedObject.infoPlate.uppercase()
        binding.tvTypeVehical.text = "Ô tô"
        if(receivedObject.pay == "false"){
            binding.tvPay.text = "Chưa thanh toán"
            binding.tvPay.setTextColor(Color.RED)
        } else {
            binding.tvPay.text = "Đã thanh toán"
            binding.tvPay.setTextColor(Color.GREEN)

        }
        binding.tvTimeCreate.text = receivedObject.dateCreate
        binding.tvPoint.text = receivedObject.accuracy.toString()
        binding.tvProvince.text = "VN"
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap =
                barcodeEncoder.encodeBitmap(receivedObject.infoPlate, BarcodeFormat.QR_CODE, 400, 400)
            binding.ivQr.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.d("TAG_A", e.toString())
        }
    }
}
