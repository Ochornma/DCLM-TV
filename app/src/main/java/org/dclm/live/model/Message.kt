package org.dclm.live.model

import android.os.Build
import android.os.Parcelable
import android.text.Html
import androidx.annotation.Keep
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.dclm.live.R

@Keep
@Parcelize
data class Message(var id: Int = 0, var title: String? =null, var audio: String? =null, var video: String? =null, var videoHigh: String? =null, var category: String? =null, var preacher: String? =null, var image: Int = R.drawable.blog_one, var seekto:Int = 0) :
    Parcelable {
    @IgnoredOnParcel
    var heading: String? = null
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    "<p align=\"justify\">" +
                            " " + title + "</p>", Html.FROM_HTML_MODE_COMPACT
                ).toString()
            } else {
                Html.fromHtml(
                    "<p align=\"justify\">" +
                            " " + title + "</p>"
                ).toString()
            }
        }


}