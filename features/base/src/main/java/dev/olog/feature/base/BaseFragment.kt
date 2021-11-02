package dev.olog.feature.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.shared.android.extensions.findInContext

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

    fun getSlidingPanel(): BottomSheetBehavior<*> {
        return (requireActivity().findInContext<HasSlidingPanel>()).getSlidingPanel()
    }

    fun restoreUpperWidgets(){
        (requireActivity() as RestorableScroll).restoreUpperWidgets()
    }

}