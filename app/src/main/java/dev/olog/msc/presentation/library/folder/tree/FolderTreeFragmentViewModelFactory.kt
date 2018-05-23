package dev.olog.msc.presentation.library.folder.tree

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import java.text.Collator
import javax.inject.Inject

class FolderTreeFragmentViewModelFactory @Inject constructor(
        private val appPreferencesUseCase: AppPreferencesUseCase,
        private val collator: Collator

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FolderTreeFragmentViewModel(
                appPreferencesUseCase,
                collator
        ) as T
    }
}