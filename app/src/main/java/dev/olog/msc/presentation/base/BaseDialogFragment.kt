package dev.olog.msc.presentation.base

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import dagger.android.support.AndroidSupportInjection
import dev.olog.msc.analytics.AppAnalytics

abstract class BaseDialogFragment : DialogFragment(), LoggableFragment {

    @CallSuper
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        AppAnalytics.logScreen(activity, this)
    }
}
