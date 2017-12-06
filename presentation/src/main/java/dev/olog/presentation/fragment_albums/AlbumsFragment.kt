package dev.olog.presentation.fragment_albums

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.isPortrait
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.withArguments
import kotlinx.android.synthetic.main.fragment_albums.view.*
import javax.inject.Inject

class AlbumsFragment : BaseFragment() {

    companion object {
        const val TAG = "FragmentAlbums"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): AlbumsFragment {
            return AlbumsFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId)
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

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }
}