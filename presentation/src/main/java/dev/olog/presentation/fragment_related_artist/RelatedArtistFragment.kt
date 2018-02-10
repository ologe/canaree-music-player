package dev.olog.presentation.fragment_related_artist

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.withArguments
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
    @Inject lateinit var viewModel: RelatedArtistViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = GridLayoutManager(context!!, 3)
        view.list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        view!!.back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        view!!.back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_related_artist
}