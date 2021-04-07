package com.apol.thebakerbois.activities

import android.os.Bundle
import android.widget.Toast
import com.apol.thebakerbois.R
import com.apol.thebakerbois.widgets.TBBButton
import com.apol.thebakerbois.widgets.TBBEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setupActionBar()

        val submit_button = findViewById<TBBButton>(R.id.btn_submit_forgot_password)

        submit_button.setOnClickListener{
            val email_forgot_pw = findViewById<TBBEditText>(R.id.et_email_forgot_pass).text.toString().trim{it <= ' '}
            if(email_forgot_pw.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_email), true)
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email_forgot_pw)
                        .addOnCompleteListener{ task->
                            hideProgressDialog()
                            if(task.isSuccessful){
                                //Shows a toast message that the task is successful and go back to login screen
                                Toast.makeText(
                                        this@ForgotPassword,
                                        resources.getString(R.string.email_sent_successfully),
                                        Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }else{
                                showErrorSnackBar(task.exception!!.message.toString(), true)
                            }
                        }
            }

        }

    }

    private fun setupActionBar(){


        var toolbar_forgot_password: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_forgotpass_activity)
        setSupportActionBar(toolbar_forgot_password)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.backbutton)
        }
        toolbar_forgot_password.setNavigationOnClickListener { onBackPressed() }
    }
}