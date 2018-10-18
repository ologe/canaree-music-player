package dev.olog.msc.presentation.base

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import dev.olog.msc.dagger.base.AndroidXInjection

abstract class BaseDialogFragment : DialogFragment() {

    @CallSuper
    override fun onAttach(context: Context?) {
        AndroidXInjection.inject(this)
        super.onAttach(context)
    }
}
