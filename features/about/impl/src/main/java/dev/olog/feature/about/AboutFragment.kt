package dev.olog.feature.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.about.api.FeatureAboutNavigator
import dev.olog.feature.about.databinding.FragmentAboutBinding
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.platform.viewBinding
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {

    companion object {
        val TAG = FragmentTagFactory.create(AboutFragment::class)
    }

    @Inject
    lateinit var navigator: FeatureAboutNavigator

    private val viewBinding by viewBinding(FragmentAboutBinding::bind)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(viewBinding) {
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.setHasFixedSize(true)

        lifecycleScope.launchWhenResumed {
            adapter.submitList(viewModel.data)
        }

        back.setOnClickListener { requireActivity().onBackPressed() }
    }

}