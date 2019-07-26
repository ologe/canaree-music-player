package dev.olog.presentation.offlinelyrics2

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragmentPresenter
import dev.olog.shared.android.extensions.subscribe
import kotlinx.android.synthetic.main.fragment_offline_lyrics_2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class OfflineLyricsFragment2 : BaseFragment() {

    @Inject
    lateinit var presenter: OfflineLyricsFragmentPresenter

    private val mediaProvider by lazy { activity as MediaProvider }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = OfflineLyricsAdapter(lifecycle)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(requireContext())

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                presenter.updateCurrentTrackId(it.id)
            }

        launch {
            presenter.observeLyrics()
                .map {
                    it.split("\n").map {
                        LyricsModel(
                            R.layout.item_offline_lyrics_2,
                            MediaId.headerId(it),
                            it,
                            false
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect { adapter.updateDataSet(it) }
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics_2
}