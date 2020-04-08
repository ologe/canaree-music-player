package dev.olog.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.main.SharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

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
        val activityViewModel by activityViewModels<SharedViewModel> { factory }
        activityViewModel.observeCurrentPlaying
            .onEach { onCurrentPlayingChanged(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun getSlidingPanel(): BottomSheetBehavior<*>? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

    fun restoreUpperWidgetsTranslation(){
        (requireActivity() as MainActivity).restoreUpperWidgetsTranslation()
    }

    protected open fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {}

}