package dev.olog.feature.about.license

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.about.R
import dev.olog.feature.about.databinding.FragmentLicensesBinding
import dev.olog.platform.viewBinding
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager

@AndroidEntryPoint
class LicensesFragment : Fragment(R.layout.fragment_licenses) {

    private val viewModel by viewModels<LicensesFragmentViewModel>()
    private val binding by viewBinding(FragmentLicensesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        val adapter = LicensesFragmentAdapter()

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        adapter.submitList(viewModel.data)

        back.setOnClickListener { requireActivity().onBackPressed() }
    }

}