package dev.olog.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation._base.BaseBottomSheetDialogFragment

class FolderDialog : BaseBottomSheetDialogFragment() {

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate()
    }
}