package dev.olog.presentation.fragment_albums

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.fragment_detail.HorizontalMarginDecoration
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

    private val marginDecorator by lazy (LazyThreadSafetyMode.NONE){ HorizontalMarginDecoration(context!!) }

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

    override fun onStart() {
        super.onStart()
        view!!.list.addItemDecoration(marginDecorator)
    }

    override fun onStop() {
        super.onStop()
        view!!.list.removeItemDecoration(marginDecorator)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_albums
}