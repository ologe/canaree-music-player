package dev.olog.presentation.recentlyadded

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.media.api.mediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.subscribe
import dev.olog.platform.extension.withArguments
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_recently_added.*
import javax.inject.Inject

@AndroidEntryPoint
class RecentlyAddedFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    companion object {
        val TAG = RecentlyAddedFragment::class.java.name
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: MediaId): RecentlyAddedFragment {
            return RecentlyAddedFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator
    private val adapter by lazyFast {
        RecentlyAddedFragmentAdapter(
            lifecycle = lifecycle,
            navigator = navigator,
            mediaProvider = mediaProvider,
            dragListener = this
        )
    }

    private val viewModel by viewModels<RecentlyAddedFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        setupDragListener(viewLifecycleOwner, list, ItemTouchHelper.LEFT)

        viewModel.observeData().subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.observeTitle()
            .subscribe(viewLifecycleOwner) { itemTitle ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    override fun provideLayoutId(): Int = R.layout.fragment_recently_added
}