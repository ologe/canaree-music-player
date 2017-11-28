package dev.olog.presentation.dialog

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.fragment_detail.DetailFragment
import dev.olog.presentation.utils.subscribe
import dev.olog.presentation.utils.withArguments
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject

class DialogItemFragment : BaseFragment() {

    companion object {
        const val TAG = "FolderDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_POSITION = "${DetailFragment.TAG}.arguments.list_position"

        fun newInstance(mediaId: String, position: Int): DialogItemFragment {
            return DialogItemFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_POSITION to position)
        }
    }

    @Inject lateinit var viewModel: DialogItemViewModel
    @Inject lateinit var adapter: DialogItemAdapter

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter

        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_item, container, false)
    }
}