package com.apol.thebakerbois.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.apol.thebakerbois.R
import com.apol.thebakerbois.firestore.FirestoreClass
import com.apol.thebakerbois.models.User
import com.apol.thebakerbois.widgets.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        val login_button= findViewById<TBBButton>(R.id.btn_login)
        login_button.setOnClickListener(this)

        val register_button = findViewById<TBBCaviarDreamsBold>(R.id.tv_register)
        register_button.setOnClickListener(this)
        val forgot_password_button = findViewById<TBBCaviarDreams>(R.id.forgot_password)
        forgot_password_button.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        if(view != null){
            //view.id returns the int id
            when(view.id){
                //FindViewById is not needed here.
                R.id.forgot_password ->{
                    val intent = Intent(this@LoginActivity, ForgotPassword::class.java)
                    startActivity(intent)
                }
                R.id.btn_login ->{
                    loginRegisteredUser()
                }
                R.id.tv_register ->{
                    val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    private fun loginRegisteredUser(){
        if(validateLoginDetails()){
            //show Progress Dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            //get the text from editText and trim its space
            val email =findViewById<TBBEditText>(R.id.et_email_login).text.toString().trim{it <= ' '}
            val password = findViewById<TBBEditText>(R.id.et_password_login).text.toString().trim{it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                    //The task variable is returned in
                    // "FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)"
                    .addOnCompleteListener {task ->
                        //hides progress dialog
                        hideProgressDialog()
                        if(task.isSuccessful){
                            FirestoreClass().getUserDetails(this@LoginActivity)
                        }else{
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
        }
    }
    private fun validateLoginDetails(): Boolean{
        val email_id=findViewById<TBBEditText>(R.id.et_email_login)
        val password=findViewById<TBBEditText>(R.id.et_password_login)
        val regex_email = Regex("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
        return when{
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
            }else ->{
                true
            }
        }
    }
    fun userLoggedInSuccess(user: User){
        //Hide progress dialog
        hideProgressDialog()

        Log.i("First Name:", user.firstName)
        Log.i("Last Name:", user.lastName)
        Log.i("Email:", user.email)

        if(user.profileCompleted == 0){
            //If user profile is not completed
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            //putExtra is like hash map. It has a name and value, in this case, it is a model or instance of user THAT IS PARCELABLE
            //It can be a sting or the model itself
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)

        }
        else{
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
        //Once we logged in, we dont want to get back to login screen/activity
        finish()
    }
}