package com.apol.thebakerbois.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class TBBEditText(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {

    init{
        applyFont()
    }

    private fun applyFont(){
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "CaviarDreams.ttf")
        setTypeface(typeFace)
    }

}