package org.dclm.live.model

import androidx.leanback.widget.HeaderItem

class IconHeaderItem : HeaderItem {
    /** Hold an icon resource id  */
    var iconResId = ICON_NONE

    @JvmOverloads
    constructor(
        id: Long,
        name: String?,
        iconResId: Int = ICON_NONE
    ) : super(id, name) {
        this.iconResId = iconResId
    }

    constructor(name: String?) : super(name)

    companion object {
        private val TAG = IconHeaderItem::class.java.simpleName
        const val ICON_NONE = -1
    }
}