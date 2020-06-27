package org.dclm.live.spinner

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

class SpinnerFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val progress = ProgressBar(container?.context)
        if (container is FrameLayout){
            val layoutParam = FrameLayout.LayoutParams(100, 100, Gravity.CENTER)
            progress.layoutParams = layoutParam
        }
        return progress
    }
}