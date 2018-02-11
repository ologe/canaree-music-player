package dev.olog.msc.domain.interactor.albums

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_albums.view.*
import javax.inject.Inject

class AlbumsFragment : BaseFragment() {

    companion object {
        const val TAG = "FragmentAlbums"
        const val ARGUMENTS_MEDIA_ID = "${TAG}.arguments.media_id"

        fun newInstance(mediaId: MediaId): AlbumsFragment {
            return AlbumsFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject lateinit var adapter : AlbumsFragmentAdapter
    @Inject lateinit var viewModel: AlbumsFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = adapter
        val spanCount = if (context!!.isPortrait) 2 else 4
        val layoutManager = GridLayoutManager(context!!, spanCount)
        view.list.layoutManager = layoutManager
    }

    override fun provideLayoutId(): Int = R.layout.fragment_albums
}