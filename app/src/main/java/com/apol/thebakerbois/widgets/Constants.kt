package com.apol.thebakerbois.widgets

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    //We have constants because we don't want the string "users" be mistyped in other parts.
    //Or if we want to change the value of users, just change it here.
    const val USERS: String = "users"
    //Keys for SHAREDPREFERENCES.
    const val THEBAKERBOIS_PREFERENCES: String = "TheBakerBoisPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE: Int = 2
    const val PICK_IMAGE_REQUEST_CODE: Int = 1
    const val FEMALE: String = "female"
    const val MALE: String = "male"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val USER_PROFILE_IMAGE: String = "User_profile_image"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
        //Launches the image selection of phone storage using the constant code
        //Starting an activity and getting a result.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        /*
        * MimeTypeMap: Two-way map that maps MIME-Types
        *
        * getSingleton(): Get the singleton instance of MimeTypeMap
        *
        * getExtensionFromMimeType: Return the registered extension for the given MIME type
        *
        * contentResolver.getType(): Return the MIME type of the given content URL
         */
        return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}