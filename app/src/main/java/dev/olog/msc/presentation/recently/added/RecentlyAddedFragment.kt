package dev.olog.msc.presentation.recently.added

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.presentation.viewModelProvider
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_recently_added.*
import kotlinx.android.synthetic.main.fragment_recently_added.view.*
import javax.inject.Inject

class RecentlyAddedFragment : BaseFragment() {

    companion object {
        const val TAG = "RecentlyAddedFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"


        fun newInstance(mediaId: MediaId): RecentlyAddedFragment {
            return RecentlyAddedFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var adapter: RecentlyAddedFragmentAdapter

    private val viewModel by lazyFast { viewModelProvider<RecentlyAddedFragmentViewModel>(viewModelFactory) }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = adapter
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.setHasFixedSize(true)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.LEFT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        viewModel.data.subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.itemTitle.subscribe(viewLifecycleOwner) { itemTitle ->
            val headersArray = resources.getStringArray(R.array.recently_added_header)
            val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
            this.header.text = header
        }
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_recently_added
}