package dev.olog.presentation.dialog_entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation._base.BaseBottomSheetDialogFragment
import dev.olog.presentation.utils.extension.withArguments
import javax.inject.Inject

class DialogItemFragment : BaseBottomSheetDialogFragment() {

    @Inject lateinit var viewModel: DialogItemViewModel

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DialogItemFragment {
            return DialogItemFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

//    fun create(context: Context, anchor: View, mediaId: String){
//        val popup = PopupMenu(context, anchor, Gravity.BOTTOM)
//        popup.inflate(provideMenuRes(mediaId))
//        popup.setOnMenuItemClickListener { item ->
//
//            viewModel.data[item.title]
//                    ?.timeout(2, TimeUnit.SECONDS)
//                    ?.subscribe(popup::dismiss, Throwable::printStackTrace)
//
//            true
//        }
//
//        popup.show()
//    }

//    @MenuRes
//    private fun provideMenuRes(mediaId: String): Int{
//        val category = MediaIdHelper.extractCategory(mediaId)
//        return when (category){
//            MediaIdHelper.MEDIA_ID_BY_FOLDER -> R.menu.dialog_folder
//            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> R.menu.dialog_playlist
//            MediaIdHelper.MEDIA_ID_BY_ALL -> R.menu.dialog_song
//            MediaIdHelper.MEDIA_ID_BY_ALBUM -> R.menu.dialog_album
//            MediaIdHelper.MEDIA_ID_BY_ARTIST -> R.menu.dialog_artist
//            MediaIdHelper.MEDIA_ID_BY_GENRE -> R.menu.dialog_genre
//            else -> throw IllegalArgumentException("invalid media id$mediaId")
//        }
//    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}