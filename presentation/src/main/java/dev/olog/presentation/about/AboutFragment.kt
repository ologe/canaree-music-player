package dev.olog.presentation.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : BaseFragment() {

    companion object {
        val TAG = AboutFragment::class.java.name
    }

    @Inject
    lateinit var navigator: NavigatorAbout

    private val viewModel by viewModels<AboutFragmentPresenter>()

    private val adapter by lazyFast {
        AboutFragmentAdapter(viewLifecycleOwner.lifecycle, navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter

        viewModel.data
            .onEach(adapter::updateDataSet)
            .launchIn(this)
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

    override fun provideLayoutId(): Int = R.layout.fragment_about
}