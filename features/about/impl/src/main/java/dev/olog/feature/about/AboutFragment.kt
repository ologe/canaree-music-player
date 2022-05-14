package dev.olog.feature.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.about.api.FeatureAboutNavigator
import dev.olog.platform.fragment.BaseFragment
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.subscribe
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : BaseFragment() {

    companion object {
        @JvmStatic
        val TAG = AboutFragment::class.java.name
    }

    @Inject
    lateinit var navigator: FeatureAboutNavigator

    private val viewModel by viewModels<AboutFragmentViewModel>()
    private val adapter by lazyFast {
        AboutFragmentAdapter(
            onHavocClick = { navigator.toHavocPage(requireActivity()) },
            onThirdPartyClick = { navigator.toLicensesFragment(requireActivity()) },
            onSpecialThanksClick = { navigator.toSpecialThanksFragment(requireActivity()) },
            onRateClick = { navigator.toMarket(requireActivity()) },
            onPrivacyPolicyClick = { navigator.toPrivacyPolicy(requireActivity()) },
            onCommunityClick = { navigator.joinCommunity(requireActivity()) },
            onBetaClick = { navigator.joinBeta(requireActivity()) },
            onChangelogClick = { navigator.toChangelog(requireActivity()) },
            onGithubClick = { navigator.toGithub(requireActivity()) },
            onTranslationsClick = { navigator.toTranslations(requireActivity()) },
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter

        viewModel.observeData()
            .subscribe(viewLifecycleOwner, adapter::submitList)
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