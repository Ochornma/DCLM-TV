package org.dclm.live.error

import android.os.Bundle
import android.view.View
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.ErrorSupportFragment
import org.dclm.live.R

class ErrorFragment: ErrorSupportFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = activity?.resources?.getString(R.string.app_name)
        imageDrawable = activity?.getDrawable(R.drawable.lb_ic_sad_cloud);
        message = resources.getString(R.string.error_fragment_message)
        setDefaultBackground(true)

        buttonText = resources.getString(R.string.dismiss_error)

        setButtonClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            activity?.supportFragmentManager?.popBackStack();
        }
    }
}