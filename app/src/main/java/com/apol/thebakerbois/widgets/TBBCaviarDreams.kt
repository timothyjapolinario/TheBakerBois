package com.apol.thebakerbois.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class TBBCaviarDreams(context: Context, attributes: AttributeSet): AppCompatTextView(context, attributes){

    init{
        applyFont()
    }
    private fun applyFont(){
        //This creates a font from the asset
        val caviarDreamsTypeFace : Typeface =
                //still don't know what the hell context is.
                Typeface.createFromAsset(context.assets, "CaviarDreams.ttf")
        //typeface is inherited from AppCompatTextView
        typeface = caviarDreamsTypeFace
    }
}