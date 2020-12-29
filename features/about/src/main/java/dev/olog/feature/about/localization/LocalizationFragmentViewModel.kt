package dev.olog.feature.about.localization

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class LocalizationFragmentViewModel @ViewModelInject constructor(

) : ViewModel() {

    companion object {

        // TODO update
        private val contributors: List<String>
            get() {
                return listOf(
                    "Μάριος Κομπούζι - Greek",
                    "Χρήστος Μπουλουγούρης - Greek",
                    "colabirb - Vietnamese"
                )
            }

    }

    val data: Flow<List<LocalizationFragmentModel>> = flowOf(buildList {
        add(LocalizationFragmentModel.Help)
        add(LocalizationFragmentModel.Header)
        addAll(contributors.map(LocalizationFragmentModel::Contributor))
    })

}