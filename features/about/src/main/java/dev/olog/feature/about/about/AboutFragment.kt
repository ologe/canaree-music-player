package dev.olog.feature.about.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.about.R
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {

    @Inject
    lateinit var navigator: Navigator

    private val viewModel by viewModels<AboutFragmentViewModel>()

    private val adapter by lazyFast {
        AboutFragmentAdapter(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter

        viewModel.data
            .onEach(adapter::submitList)
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

}