package dev.olog.presentation.recentlyadded

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_recently_added.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class RecentlyAddedFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    companion object {
        @JvmStatic
        val TAG = RecentlyAddedFragment::class.java.name
        const val ARGUMENTS_MEDIA_ID = "media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): RecentlyAddedFragment {
            return RecentlyAddedFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var navigator: Navigator
    private val adapter by lazyFast {
        RecentlyAddedFragmentAdapter(navigator, act as MediaProvider, this)
    }

    private val viewModel by viewModels<RecentlyAddedFragmentViewModel> {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.LEFT)

        viewModel.data
            .onEach { adapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.title
            .onEach { itemTitle ->
                val headersArray = resources.getStringArray(R.array.recently_added_header)
                val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
                this.header.text = header
            }.launchIn(viewLifecycleOwner.lifecycleScope)
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
        disposeDragListener()
    }

    override fun onCurrentPlayingChanged(mediaId: MediaId) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_recently_added
}