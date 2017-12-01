package dev.olog.presentation.dialog_entry

import android.support.v7.app.AppCompatActivity
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.*
import dev.olog.presentation.navigation.Navigator
import io.reactivex.Completable
import javax.inject.Inject

class DialogItemViewModel @Inject constructor(
        navigator: Navigator,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        getFolderUseCase: GetFolderUseCase,
        getPlaylistUseCase: GetPlaylistUseCase,
        getSongUseCase: GetSongUseCase,
        getAlbumUseCase: GetAlbumUseCase,
        getArtistUseCase: GetArtistUseCase,
        getGenreUseCase: GetGenreUseCase

) {

    lateinit var activity: AppCompatActivity
    lateinit var mediaId: String

    companion object {
        const val ADD_FAVORITE = "add favorite"
        const val ADD_PLAYLIST = "add playlist"
        const val ADD_QUEUE = "add queue"
        const val VIEW_INFO = "view info"
        const val VIEW_ALBUM = "view album"
        const val VIEW_ARTIST = "view artist"
        const val SHARE = "share"
        const val SET_RINGTONE = "set ringtone"
        const val RENAME = "rename"
        const val DELETE = "delete"
    }

    private val useCasesModule by lazy { DialogUseCasesModule(activity, mediaId,
            navigator, getSongListByParamUseCase,
            getFolderUseCase, getPlaylistUseCase, getSongUseCase,
            getAlbumUseCase, getArtistUseCase, getGenreUseCase) }

    val useCases: Map<String, Completable> by lazy { mapOf(
            ADD_FAVORITE to useCasesModule.provideAddQueueUseCase(),
            ADD_PLAYLIST to useCasesModule.provideAddToPlaylistUseCase(),
            ADD_QUEUE to useCasesModule.provideAddQueueUseCase(),
            VIEW_INFO to useCasesModule.provideViewAlbumUseCase(), // todo
            VIEW_ALBUM to useCasesModule.provideViewAlbumUseCase(),
            VIEW_ARTIST to useCasesModule.provideViewArtistUseCase(),
            SHARE to useCasesModule.provideShareUseCase(),
            SET_RINGTONE to useCasesModule.provideSetRingtoneUseCase(),
            RENAME to useCasesModule.provideRenamePlaylistUseCase(),
            DELETE to useCasesModule.provideDeleteUseCase()
    ) }


}