package com.example.mongodbrealmcourse.view.fragment.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.mongodbrealmcourse.databinding.FragmentChartBinding
import com.example.mongodbrealmcourse.view.fragment.BaseFragment
import com.example.mongodbrealmcourse.viewmodel.listener.HomeFragmentListener
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.example.mongodbrealmcourse.viewmodel.utils.PreferenceHelper

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