package dev.olog.feature.presentation.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.lib.media.MediaProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activityViewModel by activityViewModels<SharedViewModel>()
        activityViewModel.observeCurrentPlaying
            .onEach { onCurrentPlayingChanged(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun getSlidingPanel(): BottomSheetBehavior<*>? {
        return (requireActivity() as HasSlidingPanel).getSlidingPanel()
    }

    protected open fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {}

    protected val mediaProvider: MediaProvider
        get() = requireActivity() as MediaProvider

}