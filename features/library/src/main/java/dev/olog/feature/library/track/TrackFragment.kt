package dev.olog.feature.library.track

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.extensions.argument
import dev.olog.feature.presentation.base.extensions.withArguments
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_track.emptyStateText
import kotlinx.android.synthetic.main.fragment_track.list
import kotlinx.android.synthetic.main.fragment_track.more
import kotlinx.android.synthetic.main.fragment_track.sidebar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
internal class TrackFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance(podcast: Boolean): TrackFragment {
            return TrackFragment().withArguments(
                Params.PODCAST to podcast
            )
        }

    }

    private val isPodcast by argument<Boolean>(Params.PODCAST)

    @Inject
    lateinit var navigator: Navigator

    private val viewModel by viewModels<TrackFragmentViewModel>()

    private val adapter by lazyFast {
        TrackFragmentAdapter(mediaProvider, navigator, viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        viewModel.data
            .onEach {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
//                sidebar.onDataChanged(it) TODO
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        sidebar.scrollableLayoutId = if (isPodcast) {
            R.layout.item_tab_podcast
        } else {
            R.layout.item_tab_song
        }

        viewModel.allPodcastPositions
            .onEach { adapter.updatePodcastPositions(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener {
            // TODO restore navigation
//            navigator.toMainPopup(it, createPopupCategory())
        }
        // TODO sidebar listener
        // TODO sort listener
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
    }

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_track
}