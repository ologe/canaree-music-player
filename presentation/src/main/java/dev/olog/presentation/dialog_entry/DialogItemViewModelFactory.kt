package dev.olog.presentation.dialog_entry

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class DialogItemViewModelFactory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaId: String,
        private val item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        private val useCases: Map<String, @JvmSuppressWildcards Completable>

) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DialogItemViewModel(
                context, mediaId, item,
                useCases) as T
    }
}