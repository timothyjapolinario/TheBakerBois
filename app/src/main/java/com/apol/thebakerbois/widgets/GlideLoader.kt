package com.apol.thebakerbois.widgets

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.apol.thebakerbois.R
import com.bumptech.glide.Glide
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserPicture(imageURI: Uri, imageView: ImageView){
        try{
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
}