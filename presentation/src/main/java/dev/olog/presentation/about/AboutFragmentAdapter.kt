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
import dev.olog.shared.android.extensions.colorAccent
import kotlinx.android.synthetic.main.item_about.view.*


class AboutFragmentAdapter(
    lifecycle: Lifecycle,
    private val navigator: NavigatorAbout,
    private val presenter: AboutFragmentPresenter

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.mediaId) {
                AboutFragmentPresenter.THIRD_SW_ID -> navigator.toLicensesFragment()
                AboutFragmentPresenter.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment()
                AboutFragmentPresenter.RATE_ID -> navigator.toMarket()
                AboutFragmentPresenter.PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                AboutFragmentPresenter.BUY_PRO -> presenter.buyPro()
                AboutFragmentPresenter.COMMUNITY -> navigator.joinCommunity()
                AboutFragmentPresenter.BETA -> navigator.joinBeta()
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        if (item.mediaId == AboutFragmentPresenter.BUY_PRO) {
            val view = binding.root
            view.title.setTextColor(ColorStateList.valueOf(view.context.colorAccent()))
        }
        binding.setVariable(BR.item, item)
    }

}