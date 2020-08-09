package dev.olog.feature.library.artist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.LibrarySpanSizeLookup
import dev.olog.feature.library.LibrarySpanSizeLookup.Companion.SPAN_COUNT
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.layout.manager.ArtistSpanSizeLookup
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.extensions.withArguments
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_artist.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
internal class ArtistFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance(podcast: Boolean): ArtistFragment {
            return ArtistFragment().withArguments(
                Params.PODCAST to podcast
            )
        }

    }

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        ArtistFragmentAdapter(navigator)
    }

    private val viewModel by viewModels<ArtistFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanLookup = LibrarySpanSizeLookup(viewModel.getSpanCount())
        val layoutManager = OverScrollGridLayoutManager(list, SPAN_COUNT)
        layoutManager.spanSizeLookup = spanLookup

        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

        sidebar.scrollableLayoutId = R.layout.item_tab_album

        viewModel.data
            .onEach {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
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

    override fun provideLayoutId(): Int = R.layout.fragment_artist
}