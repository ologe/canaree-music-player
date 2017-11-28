package dev.olog.presentation.dialog

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.model.DisplayableItem
import kotlinx.android.synthetic.main.fragment_tab.view.*

class FolderDialog : BaseFragment() {

    companion object {
        const val TAG = "FolderDialog"
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderDialogAdapter(lifecycle)
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter

        adapter.updateDataSet(listOf(
                DisplayableItem(R.layout.item_dialog_image, "1", "More Life", "Drake"),
                DisplayableItem(R.layout.item_dialog_text, "2", context!!.getString(R.string.popup_play)),
                DisplayableItem(R.layout.item_dialog_text, "3", context!!.getString(R.string.popup_play_shuffle)),
                DisplayableItem(R.layout.item_dialog_text, "4", context!!.getString(R.string.popup_add_to_playlist)),
                DisplayableItem(R.layout.item_dialog_text, "5", context!!.getString(R.string.popup_add_to_queue)),
                DisplayableItem(R.layout.item_dialog_text, "6", context!!.getString(R.string.popup_share)),
                DisplayableItem(R.layout.item_dialog_text, "7", context!!.getString(R.string.popup_delete))
        ))
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_artist, container, false)
    }
}