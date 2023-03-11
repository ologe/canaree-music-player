package dev.olog.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import dev.olog.presentation.interfaces.HasRestorableWidgets
import dev.olog.platform.extension.findInContext

abstract class BaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(provideLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    fun restoreUpperWidgetsTranslation(){
        (requireActivity().findInContext<HasRestorableWidgets>()).restoreUpperWidgetsTranslation()
    }

}