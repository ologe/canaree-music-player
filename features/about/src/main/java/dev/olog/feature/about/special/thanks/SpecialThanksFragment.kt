package dev.olog.feature.about.special.thanks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.about.R
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_special_thanks.*

@AndroidEntryPoint
internal class SpecialThanksFragment : Fragment(R.layout.fragment_special_thanks) {

    private val viewModel by viewModels<SpecialThanksFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = OverScrollLinearLayoutManager(list)
        val adapter = SpecialThanksFragmentAdapter()

        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

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