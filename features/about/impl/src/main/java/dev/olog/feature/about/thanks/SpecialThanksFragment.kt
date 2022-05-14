package dev.olog.feature.about.thanks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.about.R
import dev.olog.feature.about.databinding.FragmentSpecialThanksBinding
import dev.olog.platform.viewBinding
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager

@AndroidEntryPoint
class SpecialThanksFragment : Fragment(R.layout.fragment_special_thanks) {

    private val viewModel by viewModels<SpecialThanksViewModel>()
    private val binding by viewBinding(FragmentSpecialThanksBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        val layoutManager = OverScrollLinearLayoutManager(list)
        val adapter = SpecialThanksFragmentAdapter()
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

        adapter.submitList(viewModel.data)

        back.setOnClickListener { requireActivity().onBackPressed() }
    }

}