package dev.olog.feature.library.album

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
internal class AlbumFragment : BaseFragment() {

    private val viewModel by viewModels<AlbumFragmentViewModel>()

    @Inject
    lateinit var navigator: Navigator

//    private val adapter by lazyFast {
//        AlbumFragmentAdapter(navigator)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val spanLookup = LibrarySpanSizeLookup(viewModel.getSpanCount())
//        val layoutManager = OverScrollGridLayoutManager(list, LibrarySpanSizeLookup.SPAN_COUNT)
//        layoutManager.spanSizeLookup = spanLookup

//        list.adapter = adapter
//        list.layoutManager = layoutManager
//        list.setHasFixedSize(true)

//        sidebar.scrollableLayoutId = R.layout.item_tab_album

        viewModel.data
            .onEach {
//                adapter.submitList(it)
//                emptyStateText.isVisible = it.isEmpty()
//                sidebar.onDataChanged(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        // TODO listeners
    }

    override fun onPause() {
        super.onPause()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_album
}