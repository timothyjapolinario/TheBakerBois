package com.apol.thebakerbois.activities

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.apol.thebakerbois.R
import com.apol.thebakerbois.widgets.Constants

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.THEBAKERBOIS_PREFERENCES, Context.MODE_PRIVATE)
        //defValue is Default Value, which means if the LOGGED_IN_USERNAME is empty, then defValue is returned.
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")

        findViewById<TextView>(R.id.tv_main).text = "${username}"

    }
}