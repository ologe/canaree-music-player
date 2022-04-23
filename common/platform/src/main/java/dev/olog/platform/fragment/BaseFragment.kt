package dev.olog.platform.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.platform.HasScrollableContent
import dev.olog.platform.HasSlidingPanel
import dev.olog.shared.extension.findInContext

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
        return (requireContext().findInContext<HasSlidingPanel>()).getSlidingPanel()
    }

    fun restoreToInitialTranslation(){
        (requireActivity() as HasScrollableContent).restoreToInitialTranslation()
    }

}