package dev.olog.msc.presentation.about

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dagger.Lazy
import dev.olog.msc.BR
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.NavigatorAbout
import dev.olog.msc.utils.k.extension.setOnClickListener
import javax.inject.Inject


class AboutActivityAdapter @Inject constructor(
        @ActivityLifecycle lifecycle: Lifecycle,
        private val navigator: Lazy<NavigatorAbout>,
        private val presenter: AboutActivityPresenter

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            when (item.mediaId){
//                AboutActivityPresenter.AUTHOR_ID -> navigator.get().toDeveloperProfile()
                AboutActivityPresenter.THIRD_SW_ID -> navigator.get().toLicensesFragment()
                AboutActivityPresenter.SPECIAL_THANKS_ID -> navigator.get().toSpecialThanksFragment()
                AboutActivityPresenter.RATE_ID -> navigator.get().toMarket()
                AboutActivityPresenter.PRIVACY_POLICY -> navigator.get().toPrivacyPolicy()
                AboutActivityPresenter.BUY_PRO -> presenter.buyPro()
                AboutActivityPresenter.COMMUNITY -> navigator.get().joinCommunity()
                AboutActivityPresenter.BETA -> navigator.get().joinBeta()
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

}