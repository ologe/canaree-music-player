package dev.olog.feature.detail.recently.added

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.adapter.drag.DragListenerImpl
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.detail.R
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_recently_added.*
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class RecentlyAddedFragment : Fragment(R.layout.fragment_recently_added), IDragListener by DragListenerImpl() {

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        RecentlyAddedFragmentAdapter(
            navigator = navigator,
            mediaProvider = requireActivity().mediaProvider,
            dragListener = this
        )
    }

    private val viewModel by viewModels<RecentlyAddedFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.LEFT)

        viewModel.observeData()
            .onEach(adapter::submitList)
            .launchIn(this)

        viewModel.observeTitle()
            .onEach { itemTitle ->
                val headersArray = resources.getStringArray(R.array.recently_added_header)
                val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
                this.header.text = header
            }.launchIn(this)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }
}