package com.apol.thebakerbois.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import com.apol.thebakerbois.R
import com.apol.thebakerbois.firestore.FirestoreClass
import com.apol.thebakerbois.models.User
import com.apol.thebakerbois.widgets.TBBEditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupActionBar()
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
        val login_button = findViewById<com.apol.thebakerbois.widgets.TBBCaviarDreams>(R.id.already_have_account_login)
        login_button.setOnClickListener {
            // Launch the register screen when the user clicks on the text.
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val register_button = findViewById<com.apol.thebakerbois.widgets.TBBButton>(R.id.btn_register)
        register_button.setOnClickListener {
            registerUser()
        }
    }
    private fun setupActionBar(){


        var toolbar_register: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar_register)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.backbutton)
        }
        toolbar_register.setNavigationOnClickListener { onBackPressed() }
    }


    //Validate user entries

    private fun validateRegisterDetails(): Boolean{
        val first_name = findViewById<com.apol.thebakerbois.widgets.TBBEditText>(R.id.et_first_name)
        val last_name =  findViewById<com.apol.thebakerbois.widgets.TBBEditText>(R.id.et_last_name)
        val email_id = findViewById<com.apol.thebakerbois.widgets.TBBEditText>(R.id.et_email_id)
        val password = findViewById<com.apol.thebakerbois.widgets.TBBEditText>(R.id.et_password)
        val confirm_password = findViewById<com.apol.thebakerbois.widgets.TBBEditText>(R.id.et_confirm_password)
        val terms_and_condition_check_box = findViewById<CheckBox>(R.id.checkBox_terms_and_condition)
        //Regex found on the internet
        val regex_email = Regex("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
        val regex_name = Regex("(i?)(^[a-zA-z])((?![ .,'-]\$)[a-zA-z .,'-]){0,24}")
        return when{
            //Lambda functions
            //trim removes the spaces at start and end.
           TextUtils.isEmpty(first_name.text.toString().trim{it <= ' '}) ->{
               showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
               false
           }
            !(regex_name.matches(first_name.text.toString())) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_name), true)
                false
            }
            TextUtils.isEmpty(last_name.text.toString().trim{it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            !(regex_name.matches(last_name.text.toString())) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_name), true)
                false
            }
            TextUtils.isEmpty(email_id.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            !(regex_email.matches(email_id.text.toString())) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_email), true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim{it <= ' '})  || password.text.toString().length < 8 ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(confirm_password.text.toString().trim{it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }
            password.text.toString() != confirm_password.text.toString() ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_password_mismatch), true)
                false
            }
            !terms_and_condition_check_box.isChecked()->{
                showErrorSnackBar(resources.getString(R.string.err_msg_check_terms_and_conditions), true)
                false
            }
            else ->{
                true
            }
        }
    }
    private fun registerUser(){
        if(validateRegisterDetails()){

            showProgressDialog(resources.getString(R.string.please_wait))

            val email_id = findViewById<com.apol.thebakerbois.widgets.TBBEditText>(R.id.et_email_id)
            val password = findViewById<com.apol.thebakerbois.widgets.TBBEditText>(R.id.et_password)
            val email_register: String = email_id.text.toString().trim{it <= ' '}
            val password_register: String = password.text.toString().trim{it <= ' '}
            val firstName = findViewById<TBBEditText>(R.id.et_first_name)
            val lastName = findViewById<TBBEditText>(R.id.et_last_name)

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_register,password_register)
                    .addOnCompleteListener(
                            OnCompleteListener<AuthResult>{ task ->
                                //hide progress dialog after user is created or not.
                                hideProgressDialog()
                                if (task.isSuccessful){
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    //User is the class from the models package in app, not the user in Firebase
                                    val user = User(
                                        firebaseUser.uid,
                                        firstName.text.toString().trim{it <= ' '},
                                        lastName.text.toString().trim{it <= ' '},
                                        email_id.text.toString().trim{it <= ' '}
                                    )

                                    FirestoreClass().registerUser(this@RegisterActivity,user)

                                    //Need to signout since, in firebase, when registered, it automatically logsin.
                                    FirebaseAuth.getInstance().signOut()
                                    finish()
                                }else{
                                    hideProgressDialog()
                                    //Registering not successful
                                    showErrorSnackBar(task.exception!!.message.toString(), true)
                                }
                            })
        }
    }
    fun userRegistrationSuccess(){
        //hide progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.registry_successful),
            Toast.LENGTH_LONG
        ).show()
    }
}