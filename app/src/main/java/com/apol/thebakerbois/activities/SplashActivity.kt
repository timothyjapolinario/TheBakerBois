package com.apol.thebakerbois.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.apol.thebakerbois.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //Only the parameterless constructor is deprecated,
        // it is now preferred that you specify the Looper in the constructor via the Looper.getMainLooper() method.
        Handler(Looper.getMainLooper()).postDelayed(
            {
                //Launch Activity
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish() //Call this when your activity is done and should be closed
            },
            2500
        )
    }
}