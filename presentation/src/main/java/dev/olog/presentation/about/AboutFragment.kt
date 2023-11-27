package dev.olog.presentation.about

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.pro.IBilling
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : BaseFragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter

        presenter.observeData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_about
}