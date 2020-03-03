package dev.olog.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import dev.olog.core.MediaId
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.main.MainActivityViewModel
import dev.olog.shared.android.extensions.subscribe
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(provideLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activityViewModel by activityViewModels<MainActivityViewModel> { factory }
        activityViewModel.observeCurrentPlaying.subscribe(viewLifecycleOwner) {
            onCurrentPlayingChanged(it)
        }
    }

    fun getSlidingPanel(): BottomSheetBehavior<*>? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

    fun restoreUpperWidgetsTranslation(){
        (requireActivity() as MainActivity).restoreUpperWidgetsTranslation()
    }

    protected open fun onCurrentPlayingChanged(mediaId: MediaId) {}

}