package org.dclm.live.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Category(var id: Int, var category: String) : Parcelable {
}