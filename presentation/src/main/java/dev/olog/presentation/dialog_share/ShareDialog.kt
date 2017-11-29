package dev.olog.presentation.dialog_share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.withArguments

class ShareDialog : BaseFragment() {

    companion object {
        const val TAG = "ShareDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_POSITION = "$TAG.arguments.list_position"

        fun newInstance(mediaId: String, position: Int): ShareDialog {
            return ShareDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_POSITION to position
            )
        }
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}