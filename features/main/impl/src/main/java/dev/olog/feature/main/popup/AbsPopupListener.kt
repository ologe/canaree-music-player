package dev.olog.feature.main.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.widget.PopupMenu
import androidx.core.text.parseAsHtml
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.edit.api.FeatureEditNavigator
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.shared.FileProvider
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class AbsPopupListener(
    getPlaylistBlockingUseCase: GetPlaylistsUseCase,
    private val addToPlaylistUseCase: AddToPlaylistUseCase,
    private val podcastPlaylist: Boolean

) : PopupMenu.OnMenuItemClickListener {

    val playlists by lazyFast {
        getPlaylistBlockingUseCase.execute(
            if (podcastPlaylist) PlaylistType.PODCAST
            else PlaylistType.TRACK
        )
    }

    @SuppressLint("RxLeakedSubscription")
    protected fun onPlaylistSubItemClick(
        context: Context,
        itemId: String,
        mediaId: MediaId,
        listSize: Int,
        title: String
    ) {
        playlists.firstOrNull { it.id == itemId }?.run {
            GlobalScope.launch {
                try {
                    addToPlaylistUseCase(this@run, mediaId)
                    createSuccessMessage(
                        context,
                        itemId,
                        mediaId,
                        listSize,
                        title
                    )
                } catch (ex: Throwable){
                    createErrorMessage(context)
                }
            }
        }
    }

    private suspend fun createSuccessMessage(
        context: Context,
        playlistId: String,
        mediaId: MediaId,
        listSize: Int,
        title: String
    ) = withContext(Dispatchers.Main) {
        val playlist = playlists.first { it.id == playlistId }.title
        val message = if (mediaId.isLeaf) {
            context.getString(localization.R.string.added_song_x_to_playlist_y, title, playlist)
        } else {
            context.resources.getQuantityString(
                localization.R.plurals.xx_songs_added_to_playlist_y,
                listSize,
                listSize,
                playlist
            )
        }
        context.toast(message)
    }

    private suspend fun createErrorMessage(context: Context) = withContext(Dispatchers.Main) {
        context.toast(context.getString(localization.R.string.popup_error_message))
    }

    protected fun share(activity: Activity, song: Song) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        val uri = FileProvider.getUriForPath(activity, song.path)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "audio/*"
        grantUriPermission(activity, intent, uri)
        try {
            if (intent.resolveActivity(activity.packageManager) != null) {
                val string = activity.getString(localization.R.string.share_song_x, song.title)
                activity.startActivity(Intent.createChooser(intent, string.parseAsHtml()))
            } else {
                activity.toast(localization.R.string.song_not_shareable)
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            activity.toast(localization.R.string.song_not_shareable)
        }
    }

    private fun grantUriPermission(context: Context, intent: Intent, uri: Uri){
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val resInfoList = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    protected fun viewInfo(
        activity: FragmentActivity,
        navigator: FeatureEditNavigator,
        mediaId: MediaId
    ) {
        navigator.toEditInfoFragment(activity, mediaId)
    }

    protected fun viewAlbum(
        activity: FragmentActivity,
        navigator: FeatureDetailNavigator,
        mediaId: MediaId
    ) {
        navigator.toDetail(activity, mediaId)
    }

    protected fun viewArtist(
        activity: FragmentActivity,
        navigator: FeatureDetailNavigator,
        mediaId: MediaId
    ) {
        navigator.toDetail(activity, mediaId)
    }

    protected fun setRingtone(
        activity: FragmentActivity,
        featureMainNavigator: FeatureMainNavigator,
        mediaId: MediaId,
        song: Song
    ) {
        featureMainNavigator.toSetRingtoneDialog(activity, mediaId, song.title, song.artist)
    }


}