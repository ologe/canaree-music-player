package dev.olog.presentation.recentlyadded

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.presentation.base.BaseFragment
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.extensions.viewModelProvider
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.extensions.withArguments
import kotlinx.android.synthetic.main.fragment_recently_added.*
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

    private val viewModel by lazyFast {
        viewModelProvider<RecentlyAddedFragmentViewModel>(
            viewModelFactory
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.adapter = adapter
        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        list.setHasFixedSize(true)

//        val callback = TouchHelperAdapterCallback(
//            adapter,
//            ItemTouchHelper.LEFT
//        )
//        val touchHelper = ItemTouchHelper(callback)
//        touchHelper.attachToRecyclerView(view.list)
//        adapter.touchHelper = touchHelper

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