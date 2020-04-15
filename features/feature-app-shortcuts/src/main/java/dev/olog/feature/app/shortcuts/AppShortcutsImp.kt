package dev.olog.feature.app.shortcuts

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dev.olog.core.constants.MusicServiceAction
import dev.olog.core.constants.MusicServiceCustomAction
import dev.olog.domain.MediaId
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.image.loader.getCachedBitmap
import dev.olog.navigation.screens.Activities
import dev.olog.navigation.screens.ActivitiesMap
import dev.olog.shared.coroutines.fireAndForget
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AppShortcutsImp @Inject constructor(
    private val context: Context,
    private val schedulers: Schedulers,
    private val activities: ActivitiesMap
): AppShortcuts {

    init {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
        ShortcutManagerCompat.addDynamicShortcuts(
            context, listOf(
                playlistChooser(), search(), shuffle(), play()
            )
        )
    }

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            GlobalScope.fireAndForget {
                // TODO show message
                val clazz = activities[Activities.MAIN] ?: return@fireAndForget
                val intent = Intent(context, clazz)
                intent.action = Shortcuts.DETAIL
                intent.putExtra(Shortcuts.DETAIL_EXTRA_ID, mediaId.toString())

                val bitmap = context.getCachedBitmap(mediaId, 128, { circleCrop() })
                val shortcut = ShortcutInfoCompat.Builder(context, title)
                    .setShortLabel(title)
                    .setIcon(IconCompat.createWithBitmap(bitmap))
                    .setIntent(intent)
                    .build()

                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
                withContext(schedulers.main) {
                    onAddedSuccess(context)
                }
            }


        } else {
            onAddedNotSupported(context)
        }
    }

    private fun onAddedSuccess(context: Context) {
        Toast.makeText(context,
            R.string.app_shortcut_added_to_home_screen, Toast.LENGTH_SHORT)
            .show()
    }

    private fun onAddedNotSupported(context: Context) {
        Toast.makeText(context,
            R.string.app_shortcut_add_to_home_screen_not_supported, Toast.LENGTH_SHORT)
            .show()
    }

    private fun search(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context,
            Shortcuts.SEARCH
        )
            .setShortLabel(context.getString(R.string.shortcut_search))
            .setIcon(IconCompat.createWithResource(context,
                R.drawable.shortcut_search
            ))
            .setIntent(createSearchIntent())
            .build()
    }

    private fun play(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context,
            Shortcuts.PLAY
        )
            .setShortLabel(context.getString(R.string.shortcut_play))
            .setIcon(IconCompat.createWithResource(context,
                R.drawable.shortcut_play
            ))
            .setIntent(createPlayIntent())
            .build()
    }

    private fun shuffle(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context,
            Shortcuts.SHUFFLE
        )
            .setShortLabel(context.getString(R.string.shortcut_shuffle))
            .setIcon(IconCompat.createWithResource(context,
                R.drawable.shortcut_shuffle
            ))
            .setIntent(createShuffleIntent())
            .build()
    }

    private fun playlistChooser(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context,
            Shortcuts.PLAYLIST_CHOOSER
        )
            .setShortLabel(context.getString(R.string.shortcut_playlist_chooser))
            .setIcon(IconCompat.createWithResource(context,
                R.drawable.shortcut_playlist_add
            ))
            .setIntent(createPlaylistChooserIntent())
            .build()
    }

    private fun createSearchIntent(): Intent {
        val clazz = activities[Activities.MAIN] ?: TODO("show message")
        val intent = Intent(context, clazz)
        intent.action = Shortcuts.SEARCH
        return intent
    }

    private fun createPlayIntent(): Intent {
        val clazz = activities[Activities.SHORTCUTS] ?: TODO("show message")
        val intent = Intent(context, clazz)
        intent.action = MusicServiceAction.PLAY.name
        return intent
    }

    private fun createShuffleIntent(): Intent {
        val clazz = activities[Activities.SHORTCUTS] ?: TODO("show message")
        val intent = Intent(context, clazz)
        intent.action = MusicServiceCustomAction.SHUFFLE.name
        return intent
    }

    private fun createPlaylistChooserIntent(): Intent {
        val clazz = activities[Activities.PLAYLIST_CHOOSER] ?: TODO("show message")
        val intent = Intent(context, clazz)
        intent.action = Shortcuts.PLAYLIST_CHOOSER
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return intent
    }

}