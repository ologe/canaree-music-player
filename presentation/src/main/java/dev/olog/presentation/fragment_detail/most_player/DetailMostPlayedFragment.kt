package dev.olog.presentation.fragment_detail.most_player

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.fragment_detail.DetailFragmentViewModel
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.withArguments
import kotlinx.android.synthetic.main.fragment_detail_most_played.view.*
import javax.inject.Inject

class DetailMostPlayedFragment : BaseFragment() {

    companion object {
        private const val TAG = "DetailFragment"
        private const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DetailMostPlayedFragment {
            return DetailMostPlayedFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }


    @Inject lateinit var adapter: DetailFragmentMostPlayedAdapter
    @Inject lateinit var recycledPool: RecyclerView.RecycledViewPool
    private lateinit var layoutManager : GridLayoutManager
    @Inject lateinit var viewModel: DetailFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.mostPlayedFlowable
                .subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context!!,
                5, GridLayoutManager.HORIZONTAL, false)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.recycledViewPool = recycledPool

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(view.list)


    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail_most_played
}