package org.dclm.live.presenter

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.BaseCardView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import org.dclm.live.model.Message

class MyMediaPresenter(val context: Context) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val icv = ImageCardView(parent?.context)
        icv.cardType = BaseCardView.CARD_TYPE_INFO_UNDER_WITH_EXTRA
        icv.infoVisibility = BaseCardView.CARD_REGION_VISIBLE_ACTIVATED
        // icv.setInfoAreaBackgroundColor(Color.WHITE)
        icv.isFocusable = true
        icv.isFocusableInTouchMode = true
        //icv.setBackgroundColor(Color.WHITE)
        return ViewHolder(icv)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val row: Message = item as Message
        val icv = viewHolder?.view as ImageCardView
        icv.mainImage = row.image.let { context.let { it1 -> ContextCompat.getDrawable(it1, it) } }
        icv.setMainImageDimensions(300, 300)
        icv.titleText = row.heading
        icv.contentText = row.category

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}