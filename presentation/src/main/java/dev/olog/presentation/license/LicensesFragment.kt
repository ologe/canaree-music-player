package dev.olog.presentation.license

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentLicensesBinding
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.viewBinding

class LicensesFragment : Fragment(R.layout.fragment_licenses) {

    companion object {
        @JvmStatic
        val TAG = LicensesFragment::class.java.name
    }

    private val binding by viewBinding(FragmentLicensesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = LicensesFragmentPresenter(act.applicationContext)
        val adapter = LicensesFragmentAdapter()

        binding.list.adapter = adapter
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)

        adapter.submitList(presenter.data)
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