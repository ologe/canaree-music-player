package dev.olog.feature.shortcuts

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.feature.media.MusicServiceAction
import dev.olog.feature.media.MusicServiceCustomAction
import dev.olog.feature.playlist.FeaturePlaylistNavigator
import dev.olog.image.provider.getCachedBitmap
import dev.olog.intents.Classes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppShortcutsImp @Inject constructor(
    @ApplicationContext private val context: Context,
    private val featurePlaylistNavigator: FeaturePlaylistNavigator,
) : AppShortcuts {

    private var job: Job? = null

    override fun setup() {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
        ShortcutManagerCompat.addDynamicShortcuts(
            context, listOf(
                playlistChooser(), search(), shuffle(), play()
            )
        )
    }

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            job = GlobalScope.launch {
                val intent = Intent(context, Class.forName(Classes.ACTIVITY_MAIN))
                intent.action = Shortcuts.DETAIL
                intent.putExtra(Shortcuts.DETAIL_EXTRA_ID, mediaId.toString())

                val bitmap = context.getCachedBitmap(mediaId, 128, { circleCrop() })
                val shortcut = ShortcutInfoCompat.Builder(context, title)
                    .setShortLabel(title)
                    .setIcon(IconCompat.createWithBitmap(bitmap))
                    .setIntent(intent)
                    .build()

                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
                withContext(Dispatchers.Main) {
                    onAddedSuccess(context)
                }
            }


        } else {
            onAddedNotSupported(context)
        }
    }

    private fun onAddedSuccess(context: Context) {
        Toast.makeText(context, R.string.app_shortcut_added_to_home_screen, Toast.LENGTH_SHORT)
            .show()
    }

    private fun onAddedNotSupported(context: Context) {
        Toast.makeText(context, R.string.app_shortcut_add_to_home_screen_not_supported, Toast.LENGTH_SHORT)
            .show()
    }

    private fun search(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, Shortcuts.SEARCH)
            .setShortLabel(context.getString(R.string.shortcut_search))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_search))
            .setIntent(createSearchIntent())
            .build()
    }

    private fun play(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, Shortcuts.PLAY)
            .setShortLabel(context.getString(R.string.shortcut_play))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_play))
            .setIntent(createPlayIntent())
            .build()
    }

    private fun shuffle(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, Shortcuts.SHUFFLE)
            .setShortLabel(context.getString(R.string.shortcut_shuffle))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_shuffle))
            .setIntent(createShuffleIntent())
            .build()
    }

    private fun playlistChooser(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, Shortcuts.PLAYLIST_CHOOSER)
            .setShortLabel(context.getString(R.string.shortcut_playlist_chooser))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_playlist_add))
            .setIntent(createPlaylistChooserIntent())
            .build()
    }

    private fun createSearchIntent(): Intent {
        val intent = Intent(context, Class.forName(Classes.ACTIVITY_MAIN))
        intent.action = Shortcuts.SEARCH
        return intent
    }

    private fun createPlayIntent(): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicServiceAction.PLAY.name
        return intent
    }

    private fun createShuffleIntent(): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = MusicServiceCustomAction.SHUFFLE.name
        return intent
    }

    private fun createPlaylistChooserIntent(): Intent {
        val intent = featurePlaylistNavigator.playlistChooserIntent(context)
        intent.action = Shortcuts.PLAYLIST_CHOOSER
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return intent
    }

}