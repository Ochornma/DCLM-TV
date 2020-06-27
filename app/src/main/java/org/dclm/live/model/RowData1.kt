package org.dclm.live.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class RowData1 (var image: Int?, var name: String, var link:String, var api:String) :
    Parcelable {

    }