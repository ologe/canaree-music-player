package dev.olog.msc.presentation.related.artists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.shared.lazyFast
import dev.olog.shared.viewModelProvider
import dev.olog.core.MediaId
import dev.olog.shared.act
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.withArguments
import kotlinx.android.synthetic.main.fragment_related_artist.*
import kotlinx.android.synthetic.main.fragment_related_artist.view.*
import javax.inject.Inject

class RelatedArtistFragment: BaseFragment() {

    companion object {
        const val TAG = "RelatedArtistFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"


        fun newInstance(mediaId: MediaId): RelatedArtistFragment {
            return RelatedArtistFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject lateinit var adapter: RelatedArtistFragmentAdapter
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazyFast {
        viewModelProvider<RelatedArtistFragmentViewModel>(
            viewModelFactory
        )
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context!!, if (isPortrait()) 2 else 3)
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        viewModel.data.subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.itemTitle.subscribe(viewLifecycleOwner) { itemTitle ->
            val headersArray = resources.getStringArray(R.array.related_artists_header)
            val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
            this.header.text = header
        }
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_related_artist
}