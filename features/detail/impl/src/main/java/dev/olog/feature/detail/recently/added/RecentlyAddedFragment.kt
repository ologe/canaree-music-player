package dev.olog.feature.detail.recently.added

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.detail.R
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.feature.media.api.MediaProvider
import dev.olog.platform.adapter.drag.DragListenerImpl
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.fragment.BaseFragment
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.subscribe
import dev.olog.shared.extension.withArguments
import dev.olog.ui.adapter.drag.CircularRevealAnimationController
import kotlinx.android.synthetic.main.fragment_recently_added.*
import javax.inject.Inject

@AndroidEntryPoint
class RecentlyAddedFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    companion object {
        @JvmStatic
        val TAG = RecentlyAddedFragment::class.java.name
        @JvmStatic
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): RecentlyAddedFragment {
            return RecentlyAddedFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    private val mediaProvider: MediaProvider
        get() = requireActivity().findInContext()

    @Inject
    lateinit var featureMainPopupNavigator: FeatureMainPopupNavigator

    private val adapter by lazyFast {
        RecentlyAddedFragmentAdapter(
            onItemClick = { mediaProvider.playFromMediaId(it, null, null) },
            onItemLongClick = { view, mediaId ->
                featureMainPopupNavigator.toItemDialog(view, mediaId)
            },
            onSwipeLeft = { mediaProvider.addToPlayNext(it) },
            dragListener = this
        )
    }

    private val viewModel by viewModels<RecentlyAddedFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        setupDragListener(
            scope = viewLifecycleOwner.lifecycleScope,
            list = list,
            direction = ItemTouchHelper.LEFT,
            animation = CircularRevealAnimationController(),
        )

        viewModel.observeData().subscribe(viewLifecycleOwner, adapter::submitList)

        viewModel.observeTitle()
            .subscribe(viewLifecycleOwner) { itemTitle ->
                val headersArray = resources.getStringArray(localization.R.array.recently_added_header)
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