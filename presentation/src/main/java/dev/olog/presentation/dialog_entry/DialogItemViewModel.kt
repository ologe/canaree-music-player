package dev.olog.presentation.dialog_entry

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.Flowable

class DialogItemViewModel(
        context: Context,
        mediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DialogModel>>,
        useCases: Map<String, @JvmSuppressWildcards Completable>

): ViewModel(){

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

    private val addFavorite = DialogModel(R.layout.item_dialog_text, ADD_FAVORITE, context.getString(R.string.popup_add_to_favorites), useCase = useCases[ADD_FAVORITE])
    private val addToPlaylist = DialogModel(R.layout.item_dialog_text, ADD_PLAYLIST, context.getString(R.string.popup_add_to_playlist), useCase = useCases[ADD_PLAYLIST])
    private val addToQueue = DialogModel(R.layout.item_dialog_text, ADD_QUEUE, context.getString(R.string.popup_add_to_queue), useCase = useCases[ADD_QUEUE])
    private val info = DialogModel(R.layout.item_dialog_text, VIEW_INFO, context.getString(R.string.popup_view_info), useCase = useCases[VIEW_INFO])
    private val viewAlbum = DialogModel(R.layout.item_dialog_text, VIEW_ALBUM, context.getString(R.string.popup_view_album), useCase = useCases[VIEW_ALBUM])
    private val viewArtist = DialogModel(R.layout.item_dialog_text, VIEW_ARTIST, context.getString(R.string.popup_view_artist), useCase = useCases[VIEW_ARTIST])
    private val share = DialogModel(R.layout.item_dialog_text, SHARE, context.getString(R.string.popup_share), useCase = useCases[SHARE])
    private val setRingtone = DialogModel(R.layout.item_dialog_text, SET_RINGTONE, context.getString(R.string.popup_set_as_ringtone), useCase = useCases[SET_RINGTONE])
    private val rename = DialogModel(R.layout.item_dialog_text, RENAME, context.getString(R.string.popup_rename), useCase = useCases[RENAME])
    private val delete = DialogModel(R.layout.item_dialog_text, DELETE, context.getString(R.string.popup_delete), useCase = useCases[DELETE])

    private val category = MediaIdHelper.extractCategory(mediaId)

    val data : LiveData<MutableList<DialogModel>> = item[category]!!
            .map {
                val result = actions.toMutableList()
                result.add(0, it.copy(subtitle = itemType.toLowerCase()))
                result
            }.asLiveData()

    private val itemType: String = when (category){
        MediaIdHelper.MEDIA_ID_BY_FOLDER -> context.getString(R.string.folder)
        MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> context.getString(R.string.playlist)
        MediaIdHelper.MEDIA_ID_BY_ALL -> context.getString(R.string.song)
        MediaIdHelper.MEDIA_ID_BY_ALBUM -> context.getString(R.string.album)
        MediaIdHelper.MEDIA_ID_BY_ARTIST -> context.getString(R.string.artist)
        MediaIdHelper.MEDIA_ID_BY_GENRE -> context.getString(R.string.genre)
        else -> ""
    }

    private val folderActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
    )

    private val playlistActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, rename, delete
    )

    private val songActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, info,
            viewAlbum, viewArtist, share, setRingtone, delete
    )

    private val albumActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, viewArtist, delete
    )

    private val artistActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
    )

    private val genreActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
    )

    private val actions : List<DialogModel> = when (category){
        MediaIdHelper.MEDIA_ID_BY_FOLDER -> folderActions
        MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistActions
        MediaIdHelper.MEDIA_ID_BY_ALL -> songActions
        MediaIdHelper.MEDIA_ID_BY_ALBUM -> albumActions
        MediaIdHelper.MEDIA_ID_BY_ARTIST -> artistActions
        MediaIdHelper.MEDIA_ID_BY_GENRE -> genreActions
        else -> mutableListOf()
    }

}