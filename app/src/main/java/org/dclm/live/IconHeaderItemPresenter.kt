package org.dclm.live

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import org.dclm.live.model.IconHeaderItem


class IconHeaderItemPresenter : RowHeaderPresenter() {
    private var mUnselectedAlpha = 0f
    override fun onCreateViewHolder(viewGroup: ViewGroup): ViewHolder {
        mUnselectedAlpha = viewGroup.resources.getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1)
        val inflater = viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.custom_header, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, o: Any) {
        val iconHeaderItem = (o as ListRow).headerItem as IconHeaderItem
        val rootView: View = viewHolder.view
        val iconView: ImageView = rootView.findViewById(R.id.header_icon) as ImageView
        val iconResId = iconHeaderItem.iconResId
        if (iconResId != IconHeaderItem.ICON_NONE) { // Show icon only when it is set.
            val icon: Drawable = rootView.resources.getDrawable(iconResId, null)
            iconView.setImageDrawable(icon)
        }
        val label = rootView.findViewById(R.id.header_label) as TextView
        label.text = iconHeaderItem.name
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        // no op
    }

    // also assumes the xml inflation will return a RowHeaderView
    override fun onSelectLevelChanged(holder: ViewHolder) {
        // this is a temporary fix
       /* holder.view.alpha = mUnselectedAlpha + holder.selectLevel *
                (1.0f - mUnselectedAlpha)*/
    }

}