package dev.olog.presentation.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.interactor.IsRepositoryEmptyUseCase
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.Permissions
import dev.olog.shared.extensions.asFlowable
import dev.olog.shared.extensions.assertBackground
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
        @ApplicationContext private val context: Context,
        private val presentationPrefs: PresentationPreferencesGateway,
        private val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) : ViewModel() {

    private val isRepositoryEmptyLiveData = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            isRepositoryEmptyUseCase.execute()
                .asFlowable()
                .asFlow()
                .assertBackground()
                .collect { isRepositoryEmptyLiveData.value = it }
        }
    }

    fun observeIsRepositoryEmpty(): LiveData<Boolean> = isRepositoryEmptyLiveData

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}