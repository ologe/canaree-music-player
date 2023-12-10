package dev.olog.presentation.translations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.databinding.FragmentTranslationsBinding
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class TranslationsFragment : Fragment(R.layout.fragment_translations) {

    @Inject
    internal lateinit var navigator: NavigatorAbout

    private val binding by viewBinding(FragmentTranslationsBinding::bind)
    private val adapter by lazyFast {
        val data = listOf("", "") + contributors
        TranslationFragmentAdapter(data.toMutableList(), navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.adapter = adapter
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
    }

    override fun onResume() {
        super.onResume()
        binding.back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        binding.back.setOnClickListener(null)
    }

    companion object {

        @JvmStatic
        val TAG = TranslationsFragment::class.java.name

        @JvmStatic
        fun newInstance(): Fragment {
            return TranslationsFragment()
        }

        @JvmStatic
        val contributors: List<String>
            get() { // TODO update, or maybe even fetch from api
                return listOf(
                    "Μάριος Κομπούζι - Greek",
                    "Χρήστος Μπουλουγούρης - Greek",
                    "colabirb - Vietnamese"
                )
            }

    }

}