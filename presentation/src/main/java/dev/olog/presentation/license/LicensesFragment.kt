package dev.olog.presentation.license

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_licenses.view.*

@AndroidEntryPoint
class LicensesFragment : Fragment(R.layout.fragment_licenses) {

    private val viewModel by viewModels<LicensesFragmentViewModel>()

    companion object {
        @JvmStatic
        val TAG = LicensesFragment::class.java.name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = LicensesFragmentAdapter()

        view.list.adapter = adapter
        view.list.layoutManager = OverScrollLinearLayoutManager(list)

        adapter.submitList(viewModel.data)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}