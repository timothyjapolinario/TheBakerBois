package com.apol.thebakerbois.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//A model class
@Parcelize
class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0
): Parcelable