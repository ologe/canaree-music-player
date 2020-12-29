package dev.olog.feature.detail.related.artist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.detail.R
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_related_artist.*
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class RelatedArtistFragment : Fragment(R.layout.fragment_related_artist) {

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        RelatedArtistFragmentAdapter(navigator)
    }

    private val viewModel by viewModels<RelatedArtistFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollGridLayoutManager(list, 2)
        list.adapter = adapter
        list.setHasFixedSize(true)

        viewModel.observeData()
            .onEach(adapter::submitList)
            .launchIn(this)

        viewModel.observeTitle()
            .onEach { itemTitle ->
                val headersArray = resources.getStringArray(R.array.related_artists_header)
                val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
                this.header.text = header
            }.launchIn(this)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}