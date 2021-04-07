package com.apol.thebakerbois.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.apol.thebakerbois.R
import com.apol.thebakerbois.widgets.TBBCaviarDreams
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    //This class is needed for activities where error might occur.

    //This variable is explained at the showProgressDialog function
    private lateinit var mProgressDialog : Dialog
    fun showErrorSnackBar(message: String, errorMessage: Boolean){
        //snackbar.make will try to find a parent
        val snackBar= Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG )
        val snackBarView = snackBar.view
        if(errorMessage){
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.colorSnackbarError
                    )
            )
        }else{
            snackBarView.setBackgroundColor(
                    //Returns the color as integers which is need in setBackGroundColor
                    //As for the context parameter? I dont know muehehe
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.colorSnackbarSuccess
                    )
            )
        }
        snackBar.show()
    }


    //The variable mProgressDialog uses lateInit because, 2 functions will be using it, and we don't want it to be null.
    //The dialog will only be initialize on the function "showProgressDialog.
    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout source.
        The resource will be inflated, adding all top-level views to the screen
         */
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.findViewById<TBBCaviarDreams>(R.id.tv_progress_text).text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the Dialog and display on the Screen.
        mProgressDialog.show()

    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}