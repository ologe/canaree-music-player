package dev.olog.presentation.translations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dev.olog.presentation.DottedDividerDecorator
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_translations.*
import javax.inject.Inject

class TranslationsFragment : BaseFragment() {

    @Inject
    internal lateinit var navigator: NavigatorAbout

    private val adapter by lazyFast {
        TranslationFragmentAdapter(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.addItemDecoration(DottedDividerDecorator(requireContext(), listOf(R.layout.item_translations_header)))
        adapter.submitList(listOf("", "") + contributors)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_translations

    companion object {

        @JvmStatic
        val TAG = TranslationsFragment::class.java.name

        @JvmStatic
        fun newInstance(): Fragment {
            return TranslationsFragment()
        }

        @JvmStatic
        // TODO update??
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