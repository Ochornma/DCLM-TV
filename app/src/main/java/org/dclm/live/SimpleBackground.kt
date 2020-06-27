package org.dclm.live

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.leanback.app.BackgroundManager


class SimpleBackground(val activity: Activity) {

    private val TAG: String = SimpleBackground::class.java.simpleName

    private val DEFAULT_BACKGROUND_RES_ID: Int = R.drawable.main
    private var mDefaultBackground: Drawable? = null

    private var mBackgroundManager: BackgroundManager? = null

    init{
        mDefaultBackground = activity.getDrawable(DEFAULT_BACKGROUND_RES_ID)
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager?.attach(activity.window)
        activity.windowManager.defaultDisplay.getMetrics(DisplayMetrics())
    }

    fun updateBackground(drawable: Drawable?) {
        mBackgroundManager!!.drawable = drawable
    }

    fun clearBackground() {
        mBackgroundManager!!.drawable = mDefaultBackground
    }
}