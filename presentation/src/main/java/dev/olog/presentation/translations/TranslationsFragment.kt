package dev.olog.presentation.translations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.lazyFast
import kotlinx.android.synthetic.main.fragment_translations.*
import javax.inject.Inject

@AndroidEntryPoint
class TranslationsFragment : BaseFragment() {

    @Inject
    internal lateinit var navigator: NavigatorAbout

    private val adapter by lazyFast {
        val data = listOf("", "") + contributors
        TranslationFragmentAdapter(data.toMutableList(), navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_translations

    companion object {

        val TAG = TranslationsFragment::class.java.name

        fun newInstance(): Fragment {
            return TranslationsFragment()
        }

        val contributors: List<String>
            get() {
                return listOf(
                    "Μάριος Κομπούζι - Greek",
                    "Χρήστος Μπουλουγούρης - Greek",
                    "colabirb - Vietnamese"
                )
            }

    }

}