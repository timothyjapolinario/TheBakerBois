package com.apol.thebakerbois.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.apol.thebakerbois.R
import com.apol.thebakerbois.firestore.FirestoreClass
import com.apol.thebakerbois.models.User
import com.apol.thebakerbois.widgets.Constants
import com.apol.thebakerbois.widgets.GlideLoader
import com.apol.thebakerbois.widgets.TBBButton
import com.apol.thebakerbois.widgets.TBBEditText
import kotlinx.android.synthetic.main.activity_register.view.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener{
    //We used lateinit because we want to globalized it.
    private lateinit var mUserDetails:User
    private lateinit var mSelectedImageFileUri: Uri
    //The "granted" variable returns a boolean whether if permission is granted or not
    //This replaces the onRequestPermissionResult
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){granted->
        if(granted){
            showErrorSnackBar("The Storage Permission is granted. Click the PlaceHolder Again.", false)
        }else{
            Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
            ).show()
        }
    }
    private val profilePictureContract = registerForActivityResult(Contract()){ uri->
        try{
            //The uri of selected image from phone storage
            val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo_profile_activity)
            mSelectedImageFileUri = uri!!
            GlideLoader(this).loadUserPicture(uri!!, iv_user_photo)
        }catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(this@UserProfileActivity,
                    resources.getString(R.string.image_selection_failed),
                    Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        var mUserDetails: User = User()
        //We checked if it has an extra so we would not crash our app.
        //The intent is this one from the login activity where we got the extra.
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            //Get the user details from intent as a ParcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        //Not letting users to change its First and Last name, and Email.
        //This can be changed depending on the design.
        val first_name_profile_activity = findViewById<TBBEditText>(R.id.et_first_name_profile_activity)
        first_name_profile_activity.isEnabled = false
        first_name_profile_activity.setText(mUserDetails.firstName)

        val last_name_profile_activity = findViewById<TBBEditText>(R.id.et_last_name_profile_activity)
        last_name_profile_activity.isEnabled = false
        last_name_profile_activity.setText(mUserDetails.lastName)

        val email_profile_activity = findViewById<TBBEditText>(R.id.et_email_profile_activity)
        email_profile_activity.isEnabled = false
        email_profile_activity.setText(mUserDetails.email.toString())


        findViewById<ImageView>(R.id.iv_user_photo_profile_activity).setOnClickListener(this@UserProfileActivity)

        findViewById<TBBButton>(R.id.btn_save_profile_activity).setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.iv_user_photo_profile_activity ->{
                    //Here, we will check if the permission is already granted or if we need to request it.
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        profilePictureContract.launch(null)
                    }else{
                        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                R.id.btn_save_profile_activity ->{

                    showProgressDialog(resources.getString(R.string.please_wait))

                    if(validateProfileUser()){
                        //showErrorSnackBar("Your Details are valid.", false)
                        val userHashMap = HashMap<String, Any>(
                        )
                        val mobileNumber = et_mobile_number_profile_activity.text.toString().trim{it <= ' '}

                        val gender = if(rb_male_profile_acitivty.isChecked){
                            Constants.MALE
                        }else{
                            Constants.FEMALE
                        }

                        if(mobileNumber.isNotEmpty()){
                            //Convert the mobilenumber(string) to a Long Type
                            userHashMap[Constants.MOBILE]=mobileNumber.toLong()
                        }

                        userHashMap[Constants.GENDER] = gender

                        showProgressDialog("Please Wait")
                        FirestoreClass().updateProfileUserData(this, userHashMap)
                        FirestoreClass().uploadImageToCloud(this@UserProfileActivity, mSelectedImageFileUri)
                    }else{
                        showErrorSnackBar("Error details",true)
                    }
                }
            }
        }
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(
                this@UserProfileActivity,
                resources.getString(R.string.update_profile_successfull),
                Toast.LENGTH_LONG
        ).show()

        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
    }

    private fun validateProfileUser(): Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number_profile_activity.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.invalid_mobile_number), true)
                false
            }
            else->{
                true
            }
        }
    }

    //This can be called "Contract for Image Selector" to remove ambiguity
    class Contract: ActivityResultContract<Void?, Uri? >(){
        override fun createIntent(context: Context, input: Void?): Intent {
            return Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        }
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }
    //This is used in the FireStoreClass.
    fun imageUploadSuccess(imageURL: String){
        hideProgressDialog()
        Toast.makeText(
                this@UserProfileActivity,
                "Your image has been uploaded successfully.",
                Toast.LENGTH_LONG
        ).show()
    }
}
/*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if(resultCode == Activity.RESULT_OK){
        if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE)
            if(data != null){

                try{
                    //The uri of selected image from phone storage
                    val selectedImageFileUri = data.data!!
                    val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo_profile_activity)
                    //iv_user_photo.setImageURI(selectedImagaweeFileUri)
                    GlideLoader(this).loadUserPicture(selectedImageFileUri, iv_user_photo)
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this@UserProfileActivity,
                    resources.getString(R.string.image_selection_failed),
                    Toast.LENGTH_LONG).show()
                }
            }

    }
}*/
/*
//This interface is the contract for receiving the results for permission requests.
//This is an old way of getting permission

override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
        //If permission granted
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showErrorSnackBar("The Storage Permission is granted", false)
            Constants.showImageChooser(this)
        }else{
            Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
            ).show()
        }
    }
}*/
/* Getting permission the old way
//Here, we will check if the permission is already granted or if we need to request it.
if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
    Constants.showImageChooser(this)
}else{
    ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.READ_STORAGE_PERMISSION_CODE)
}

 */