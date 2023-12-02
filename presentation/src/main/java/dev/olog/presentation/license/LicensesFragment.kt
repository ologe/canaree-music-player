package dev.olog.presentation.license

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentLicensesBinding
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.viewBinding

@AndroidEntryPoint
class LicensesFragment : Fragment(R.layout.fragment_licenses) {

    companion object {
        @JvmStatic
        val TAG = LicensesFragment::class.java.name
    }

    private val binding by viewBinding(FragmentLicensesBinding::bind)
    private val viewModel by viewModels<LicensesFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = LicensesFragmentAdapter()

        binding.list.adapter = adapter
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)

        adapter.submitList(viewModel.data)
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