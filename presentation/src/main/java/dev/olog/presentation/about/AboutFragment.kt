package dev.olog.presentation.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentAboutBinding
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {

    companion object {
        @JvmStatic
        val TAG = AboutFragment::class.java.name
    }

    @Inject
    lateinit var navigator: NavigatorAbout

    private val binding by viewBinding(FragmentAboutBinding::bind)
    private val viewModel by viewModels<AboutFragmentViewModel>()
    private val adapter by lazyFast {
        AboutFragmentAdapter(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter

        viewModel.observeData()
            .subscribe(viewLifecycleOwner, adapter::submitList)
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