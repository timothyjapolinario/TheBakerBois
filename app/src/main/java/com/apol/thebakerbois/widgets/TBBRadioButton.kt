package com.apol.thebakerbois.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class TBBRadioButton(context: Context, attrs: AttributeSet): AppCompatRadioButton(context, attrs) {
    init{
        applyFont()
    }
    private fun applyFont(){
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "CaviarDreamsBold.ttf")
        setTypeface(typeFace)
    }
}