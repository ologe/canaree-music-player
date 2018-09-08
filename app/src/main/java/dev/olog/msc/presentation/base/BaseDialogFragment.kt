package dev.olog.msc.presentation.base

import android.content.Context
import android.support.annotation.CallSuper
import android.support.v4.app.DialogFragment
import dagger.android.support.AndroidSupportInjection

abstract class BaseDialogFragment : DialogFragment() {

    @CallSuper
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}
