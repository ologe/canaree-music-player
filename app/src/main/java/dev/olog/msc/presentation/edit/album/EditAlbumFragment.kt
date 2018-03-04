package dev.olog.msc.presentation.edit.album

import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.withArguments

class EditAlbumFragment : BaseFragment() {

    companion object {
        const val TAG = "EditAlbumFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditAlbumFragment {
            return EditAlbumFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}