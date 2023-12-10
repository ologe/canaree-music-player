package dev.olog.presentation.recentlyadded

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.databinding.FragmentRecentlyAddedBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class RecentlyAddedFragment : Fragment(R.layout.fragment_recently_added), IDragListener by DragListenerImpl() {

    companion object {
        val TAG = RecentlyAddedFragment::class.java.name

        fun newInstance(mediaId: MediaId): RecentlyAddedFragment {
            return RecentlyAddedFragment().withArguments(
                NavigationUtils.ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator
    private val adapter by lazyFast {
        RecentlyAddedFragmentAdapter(
            lifecycle, navigator, act.findInContext(), this
        )
    }

    private val binding by viewBinding(FragmentRecentlyAddedBinding::bind) { binding ->
        binding.list.adapter = null
    }
    private val viewModel by viewModels<RecentlyAddedFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.adapter = adapter
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.setHasFixedSize(true)

        setupDragListener(binding.list, ItemTouchHelper.LEFT)

        viewModel.observeData().subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.observeTitle()
            .subscribe(viewLifecycleOwner) { itemTitle ->
                val headersArray = resources.getStringArray(R.array.recently_added_header)
                val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
                binding.header.text = header
            }
    }

    override fun onResume() {
        super.onResume()
        binding.back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        binding.back.setOnClickListener(null)
    }

}