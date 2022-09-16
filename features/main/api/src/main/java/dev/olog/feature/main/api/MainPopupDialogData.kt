package dev.olog.feature.main.api

import dev.olog.core.MediaIdCategory

sealed interface MainPopupDialogData {

    data class Library(val category: MediaIdCategory) : MainPopupDialogData
    object Search : MainPopupDialogData
    object PlayingQueue : MainPopupDialogData

}