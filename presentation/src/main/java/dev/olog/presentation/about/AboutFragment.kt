package dev.olog.presentation.about

import android.os.Bundle
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.databinding.FragmentAboutBinding
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.pro.IBilling
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import javax.inject.Inject

class AboutFragment : BaseFragment(R.layout.fragment_about) {

    companion object {
        @JvmStatic
        val TAG = AboutFragment::class.java.name
    }

    @Inject
    lateinit var navigator: NavigatorAbout
    @Inject
    lateinit var billing: IBilling
    private val presenter by lazyFast {
        AboutFragmentPresenter(ctx.applicationContext, billing)
    }
    private val adapter by lazyFast {
        AboutFragmentAdapter(lifecycle, navigator, presenter)
    }

    private val binding by viewBinding(FragmentAboutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter

        presenter.observeData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    override fun onResume() {
        super.onResume()
        binding.back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        binding.back.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

}