package com.apol.thebakerbois.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TBBCaviarDreamsBold(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {

    init{
        applyFont()
    }

    private fun applyFont(){
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "CaviarDreamsBold.ttf")
        setTypeface(typeFace)
    }
}