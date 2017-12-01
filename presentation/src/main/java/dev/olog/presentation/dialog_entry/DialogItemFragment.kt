package dev.olog.presentation.dialog_entry

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseBottomSheetDialogFragment
import dev.olog.presentation.utils.extension.removeLightStatusBar
import dev.olog.presentation.utils.extension.setLightStatusBar
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.withArguments
import dev.olog.shared.MediaIdHelper
import kotlinx.android.synthetic.main.dialog_item.view.*
import javax.inject.Inject

class DialogItemFragment : BaseBottomSheetDialogFragment(), DialogItemView {

    companion object {
        const val TAG = "DialogItemFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_POSITION = "$TAG.arguments.list_position"

        fun newInstance(mediaId: String, position: Int): DialogItemFragment {
            return DialogItemFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_POSITION to position)
        }
    }

    @Inject lateinit var viewModel: DialogItemViewModel
    @Inject lateinit var adapter: DialogItemAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.window.removeLightStatusBar()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter

        viewModel.data.subscribe(this, {

            val item = it[0]
            if (!item.canViewArtist){
                val indexOfArtist = it.indexOfFirst { it.mediaId == DialogItemViewModel.VIEW_ARTIST }
                if (indexOfArtist != -1){
                    it.removeAt(indexOfArtist)
                }
            }
            if (!item.canViewAlbum){
                val indexOfAlbum = it.indexOfFirst { it.mediaId == DialogItemViewModel.VIEW_ALBUM }
                if (indexOfAlbum != -1){
                    it.removeAt(indexOfAlbum)
                }
            }

            val category = MediaIdHelper.extractCategory(item.mediaId)
            when (category) {
                MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> {
                    val categoryValue = MediaIdHelper.extractCategoryValue(item.mediaId).toLong()
                    when (categoryValue){
                        -3000L, -4000L, -5000L -> {
                            val indexOf = it.indexOfFirst { it.mediaId == DialogItemViewModel.RENAME }
                            it.removeAt(indexOf)
                        }
                    }
                }
            }

            adapter.updateDataSet(it)
        })
    }

    override fun onDestroyView() {
        activity!!.window.setLightStatusBar()
        super.onDestroyView()
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_item, container, false)
    }

}