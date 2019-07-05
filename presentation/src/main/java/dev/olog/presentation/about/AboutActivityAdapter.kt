package dev.olog.presentation.about

import android.content.res.ColorStateList
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.presentation.BR
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.shared.extensions.colorAccent
import kotlinx.android.synthetic.main.item_about.view.*


class AboutActivityAdapter(
    lifecycle: Lifecycle,
    private val navigator: NavigatorAbout,
    private val presenter: AboutActivityPresenter

) : ObservableAdapter<DisplayableItem>(lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.mediaId) {
                AboutActivityPresenter.THIRD_SW_ID -> navigator.toLicensesFragment()
                AboutActivityPresenter.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment()
                AboutActivityPresenter.RATE_ID -> navigator.toMarket()
                AboutActivityPresenter.PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                AboutActivityPresenter.BUY_PRO -> presenter.buyPro()
                AboutActivityPresenter.COMMUNITY -> navigator.joinCommunity()
                AboutActivityPresenter.BETA -> navigator.joinBeta()
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        if (item.mediaId == AboutActivityPresenter.BUY_PRO) {
            val view = binding.root
            view.title.setTextColor(ColorStateList.valueOf(view.context.colorAccent()))
        }
        binding.setVariable(BR.item, item)
    }

}