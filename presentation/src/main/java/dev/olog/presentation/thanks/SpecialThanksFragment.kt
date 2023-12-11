package dev.olog.presentation.thanks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentSpecialThanksBinding
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.lazyFast

class SpecialThanksFragment : Fragment(R.layout.fragment_special_thanks) {

    companion object {
        @JvmStatic
        val TAG = SpecialThanksFragment::class.java.name
    }

    private val binding by viewBinding(FragmentSpecialThanksBinding::bind)
    private val presenter by lazyFast {
        SpecialThanksPresenter(act.applicationContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = OverScrollLinearLayoutManager(binding.list)
        val adapter = SpecialThanksFragmentAdapter()
        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManager
        binding.list.setHasFixedSize(true)

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