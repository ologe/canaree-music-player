package dev.olog.feature.about.license

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.about.R
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_licenses.*

@AndroidEntryPoint
internal class LicensesFragment : Fragment(R.layout.fragment_licenses) {

    private val viewModel by viewModels<LicensesFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = LicensesFragmentAdapter()

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)

        adapter.submitList(viewModel.data)
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