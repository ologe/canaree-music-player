package dev.olog.presentation.base

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import dagger.android.support.AndroidSupportInjection

abstract class BaseDialogFragment : DialogFragment() {

    @CallSuper
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}
