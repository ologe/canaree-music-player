package dev.olog.feature.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.compose.ComposeView
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.feature.media.api.mediaProvider
import dev.olog.platform.navigation.FragmentTagFactory
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    companion object {
        val TAG = FragmentTagFactory.create(SearchFragment::class)

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val viewModel by viewModels<SearchFragmentViewModel>()

    @Inject
    lateinit var featureDetailNavigator: FeatureDetailNavigator
    @Inject
    lateinit var featureBubbleNavigator: FeatureBubbleNavigator
    @Inject
    lateinit var featureMainNavigator: FeatureMainNavigator
    @Inject
    lateinit var featureMainPopupNavigator: FeatureMainPopupNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()) {
            val data = viewModel.data.collectAsState(initial = null).value ?: return@ComposeView
            val query by viewModel.query.collectAsState()

            SearchContent(
                data = data,
                query = query,
                onQueryChange = viewModel::updateQuery,
                onQueryClear = viewModel::clearQuery,
                onBubbleClick = {
                    featureBubbleNavigator.startServiceOrRequestOverlayPermission(requireActivity())
                },
                onMoreClick = {
//                    todo
//                    featureMainNavigator.toMainPopup(requireActivity(), it, null)
                },
                onPlayableClick = { mediaId ->
                    mediaProvider.playFromMediaId(mediaId, null, null)
                    viewModel.insertToRecent(mediaId)
                },
                onNonPlayableClick = { mediaId ->
                    featureDetailNavigator.toDetail(requireActivity(), mediaId)
                    viewModel.insertToRecent(mediaId)
                },
                onItemLongClick = { mediaId ->
//                    todo
//                    featureMainPopupNavigator.toItemDialog(view, mediaId)
                },
                onPlayNext = { mediaProvider.addToPlayNext(it) },
                onDelete = {
                    // todo show delete item dialog
                },
                onClearItemClick = viewModel::deleteFromRecent,
                onClearAllClick = viewModel::clearRecentSearches,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
    }

}