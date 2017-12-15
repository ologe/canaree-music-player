package dev.olog.presentation.fragment_about

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.databinding.ViewDataBinding
import android.net.Uri
import android.util.Log
import dagger.Lazy
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.extension.setOnClickListener
import javax.inject.Inject


class AboutFragmentAdapter @Inject constructor(
        @ActivityContext private val context: Context,
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Lazy<Navigator>

) : BaseListAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(dataController) { item, _ ->
            when (item.mediaId){
                AboutFragmentPresenter.THIRD_SW_ID -> navigator.get().toLicensesFragment()
                AboutFragmentPresenter.SPECIAL_THANKS_ID -> navigator.get().toSpecialThanksFragment()
                AboutFragmentPresenter.RATE_ID -> toMarket()
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    private fun toMarket(){
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.w("to rate app", "google play market not found")
        }
    }
}