package dev.olog.presentation.about

import android.os.Bundle
import android.view.View
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

class AboutFragment : BaseFragment() {

    companion object {
        @JvmStatic
        val TAG = AboutFragment::class.java.name
    }

    @Inject
    lateinit var navigator: NavigatorAbout
    private val presenter by lazyFast {
        AboutFragmentPresenter(requireContext().applicationContext)
    }
    private val adapter by lazyFast {
        AboutFragmentAdapter(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter

        adapter.submitList(presenter.data)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_about
}