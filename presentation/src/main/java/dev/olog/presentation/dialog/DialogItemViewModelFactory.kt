package dev.olog.presentation.dialog

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import io.reactivex.Flowable
import javax.inject.Inject

class DialogItemViewModelFactory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaId: String,
        private val item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>

        ) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DialogItemViewModel(
                context, mediaId, item) as T
    }
}