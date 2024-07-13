package dev.olog.presentation.relatedartists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.databinding.FragmentRelatedArtistBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.viewModelProvider
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import javax.inject.Inject

class RelatedArtistFragment : BaseFragment(R.layout.fragment_related_artist) {

    companion object {
        @JvmStatic
        val TAG = RelatedArtistFragment::class.java.name
        @JvmStatic
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): RelatedArtistFragment {
            return RelatedArtistFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var navigator: Navigator
    private val adapter by lazyFast { RelatedArtistFragmentAdapter(lifecycle, navigator) }

    private val viewModel by lazyFast {
        viewModelProvider<RelatedArtistFragmentViewModel>(
            viewModelFactory
        )
    }

    private val binding by viewBinding(FragmentRelatedArtistBinding::bind) {
        it.list.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = OverScrollGridLayoutManager(binding.list, 2)
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        viewModel.observeData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.observeTitle()
            .subscribe(viewLifecycleOwner) { itemTitle ->
                val headersArray = resources.getStringArray(R.array.related_artists_header)
                val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
                binding.header.text = header
            }
    }

    override fun onResume() {
        super.onResume()
        binding.back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        binding.back.setOnClickListener(null)
    }

}