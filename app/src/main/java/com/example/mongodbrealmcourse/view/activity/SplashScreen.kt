package com.example.mongodbrealmcourse.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mongodbrealmcourse.R
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    private val Appid = "number-plates-ayumd"
    private lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Realm.init(this)

        app = App(AppConfiguration.Builder(Appid).build())
        val credentials = Credentials.emailPassword("dhcngtvt.utt.fee@gmail.com", "Hoilamgi123")
        app.loginAsync(credentials) { result ->
            if (result.isSuccess) {
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000) // 2 seconds is the delayed time in milliseconds.
            } else {
                Toast.makeText(this, "Không kết nối được server", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
