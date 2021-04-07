package com.apol.thebakerbois.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.apol.thebakerbois.activities.LoginActivity
import com.apol.thebakerbois.activities.RegisterActivity
import com.apol.thebakerbois.activities.UserProfileActivity
import com.apol.thebakerbois.models.User
import com.apol.thebakerbois.widgets.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass{

    private val mFireStore = FirebaseFirestore.getInstance()
    //User is the class from the models package in app, not the user in Firebase
    fun registerUser(activity:RegisterActivity, userInfo: User){
        //The "users" is a collection name. If the the collection is already created, then it will not create the same one again
        mFireStore.collection(Constants.USERS)
            //Document ID for users fields. Here the document it is for the User ID.
            .document(userInfo.id)
            //Here, the userInfo are field and the SetOptions is set to merge. It is for if we want to merge later on instead of replacing the fields
            .set(userInfo, SetOptions.merge())
            .addOnCompleteListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    e
                )
            }
    }

    fun getCurrentUserID(): String{
        //An instance of the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        //A variable for the UID of current user, else it will be leave blank
        var currentUserID = ""
        if(currentUser.uid != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){
        //Here we pass the collection name from which we want the data
        mFireStore.collection(Constants.USERS)
        //The document id to get the fields of user
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.i(activity.javaClass.simpleName, document.toString())


                    //Creating an object where we save the details of that user from database.
                    //This was parcelize i think so it can be transfer to another activity
                    val user = document.toObject(User::class.java)!!


                    //This maybe a create a sharedpreference for an activity?
                    val sharedPreferences = activity.getSharedPreferences(
                            Constants.THEBAKERBOIS_PREFERENCES,
                            Context.MODE_PRIVATE
                    )
                    //To edit a sharedpreference, one must create an editor
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    //A key value pair.
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME,
                            //This is a variable place holder for string.
                            "${user.firstName} ${user.lastName}"
                    )
                    //Make sure to apply after editing.
                    editor.apply()

                    when(activity){
                        is LoginActivity ->{
                            //Call a function of base activity for transferring the result to it.
                            activity.userLoggedInSuccess(user)
                        }
                    }
                }
    }

    fun updateProfileUserData(activity: Activity, userHashMap: HashMap<String, Any>){
        mFireStore.collection((Constants.USERS))
                .document(getCurrentUserID())
                //set option merge is placed in the function "registerUser"
                .update(userHashMap)
                .addOnSuccessListener {
                    when(activity){
                        is UserProfileActivity ->{
                            activity.userProfileUpdateSuccess()
                        }
                    }
                }.
                addOnFailureListener { e->
                    when(activity){
                        is UserProfileActivity ->{
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while updating the user",
                        e
                    )
                    Toast.makeText(activity,
                    "Error while updating the user", Toast.LENGTH_LONG).show()
                }
    }

    fun uploadImageToCloud(activity: Activity, imageFileUri: Uri?){
        //This is for the name of the file or how can we reference a child
        //1. Create a reference or name for a file first
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                    +Constants.getFileExtension(
                    activity, imageFileUri!!)
        )
        //2. Use the reference variable as its reference to put it on storage
        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                    "Firebase Image File URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e(
                                "Downloadable Image URL", uri.toString()
                        )
                        when(activity){
                            is UserProfileActivity ->{
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
            }
        }.addOnFailureListener {exception ->
            when(activity){
                is UserProfileActivity ->{
                    activity.hideProgressDialog()
                }
            }
            Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
            )
        }
    }
}