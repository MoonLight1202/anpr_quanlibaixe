package com.example.mongodbrealmcourse.view.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.ActivityMain2Binding
import com.example.mongodbrealmcourse.viewmodel.callback.VoidCallback
import com.example.mongodbrealmcourse.viewmodel.utils.AnimationHelper
import com.example.mongodbrealmcourse.viewmodel.utils.PreferenceHelper
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import java.util.Locale

class ScanQRActivity : AppCompatActivity(), DecoratedBarcodeView.TorchListener {
    private lateinit var capture: CaptureManager
    private lateinit var barcode: DecoratedBarcodeView
    private var trangthai = "Off"
    private lateinit var binding: ActivityMain2Binding
    protected val preferenceHelper: PreferenceHelper by lazy { PreferenceHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val savedLanguage = preferenceHelper.current_language
        val locale = if (savedLanguage == "vi") {
            Locale("vi")
        } else {
            Locale("en")
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        setContentView(binding.root)
        barcode = binding.bcScanner
        barcode.findViewById<ImageButton>(R.id.btnFlashLight).setOnClickListener {
            AnimationHelper.scaleAnimation(it, object : VoidCallback {
                override fun execute() {
                    switchFlashlight(binding.root)
                }
            }, 0.98f)

        }
        capture = CaptureManager(this, barcode )
        capture.apply {
            initializeFromIntent(intent, savedInstanceState)
            decode()
        }
    }
    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        capture.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    fun switchFlashlight(view: View?) {
        if (trangthai.equals("Off")) {
            barcode.setTorchOn()
            onTorchOn()
            Toast.makeText(this, getString(R.string.flash_enabled), Toast.LENGTH_SHORT).show()
        } else {
            barcode.setTorchOff()
            onTorchOff()
            Toast.makeText(this, getString(R.string.flash_disabled), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTorchOn() {
        trangthai = "On"
    }

    override fun onTorchOff() {
        trangthai = "Off"
    }

}