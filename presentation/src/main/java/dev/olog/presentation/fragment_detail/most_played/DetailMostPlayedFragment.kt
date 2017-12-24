package dev.olog.presentation.fragment_detail.most_played

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.withArguments
import kotlinx.android.synthetic.main.fragment_detail_most_played.view.*
import javax.inject.Inject

class DetailMostPlayedFragment : BaseFragment() {

    companion object {
        private const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DetailMostPlayedFragment {
            return DetailMostPlayedFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }


    @Inject lateinit var adapter: DetailMostPlayedFragmentAdapter
    private lateinit var layoutManager : GridLayoutManager
    @Inject lateinit var viewModel: DetailMostPlayedFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)

        adapter.onDataChanged()
                .map { it.size }
                .filter { it in 1..5 }
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(this, { size ->
                    val span = if (size < 5) size else 5
                    layoutManager.spanCount = span
                })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context!!,
                5, GridLayoutManager.HORIZONTAL, false)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(view.list)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail_most_played
}