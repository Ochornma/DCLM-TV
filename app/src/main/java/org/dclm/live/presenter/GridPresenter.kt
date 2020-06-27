package org.dclm.live.presenter

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter

class GridPresenter : Presenter() {
    private val GRID_ITEM_WIDTH = 300
    private val GRID_ITEM_HEIGHT = 200
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view = TextView(parent!!.context)
        view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.setBackgroundColor(Color.BLACK)
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        (viewHolder!!.view as TextView).text = item as String?

        val text = viewHolder.view as TextView

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}