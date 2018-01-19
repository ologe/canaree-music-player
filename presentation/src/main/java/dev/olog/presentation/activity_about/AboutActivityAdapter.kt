package dev.olog.presentation.activity_about

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dagger.Lazy
import dev.olog.presentation.BR
import dev.olog.presentation._base.list.BaseListAdapter
import dev.olog.presentation._base.list.DataBoundViewHolder
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.NavigatorAbout
import dev.olog.presentation.utils.extension.setOnClickListener
import javax.inject.Inject


class AboutActivityAdapter @Inject constructor(
        @ActivityLifecycle lifecycle: Lifecycle,
        private val navigator: Lazy<NavigatorAbout>

) : BaseListAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(dataController) { item, _ ->
            when (item.mediaId){
                AboutActivityPresenter.AUTHOR_ID -> navigator.get().toFacebook()
                AboutActivityPresenter.THIRD_SW_ID -> navigator.get().toLicensesFragment()
                AboutActivityPresenter.SPECIAL_THANKS_ID -> navigator.get().toSpecialThanksFragment()
                AboutActivityPresenter.RATE_ID -> navigator.get().toMarket()
                AboutActivityPresenter.WEBSITE_ID -> navigator.get().toWebsite()
                AboutActivityPresenter.PRIVACY_POLICY -> navigator.get().toPrivacyPolicy()
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

}