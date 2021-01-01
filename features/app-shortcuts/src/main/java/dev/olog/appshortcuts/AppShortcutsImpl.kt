package dev.olog.appshortcuts

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.AppShortcuts
import dev.olog.domain.mediaid.MediaId
import dev.olog.lib.image.provider.getCachedBitmap
import dev.olog.navigation.Params
import dev.olog.navigation.destination.NavigationIntent
import dev.olog.navigation.destination.NavigationIntents
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.*
import javax.inject.Inject

internal class AppShortcutsImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val intents: NavigationIntents,
): AppShortcuts {

    private var job by autoDisposeJob()

    init {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
        ShortcutManagerCompat.addDynamicShortcuts(
            context, listOf(
                playlistChooser(),
                search(),
                shuffle(),
                play()
            )
        )
    }

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            val intent = intents[NavigationIntent.DETAIL]?.get() ?: return
            intent.putExtra(Params.MEDIA_ID, mediaId.toString())

            job = GlobalScope.launch {

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
        return ShortcutInfoCompat.Builder(context, ShortcutsConstants.SEARCH)
            .setShortLabel(context.getString(R.string.shortcut_search))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_search))
            .setIntent(intents[NavigationIntent.SEARCH]?.get() ?: Intent())
            .build()
    }

    private fun play(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, ShortcutsConstants.PLAY)
            .setShortLabel(context.getString(R.string.shortcut_play))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_play))
            .setIntent(intents[NavigationIntent.SHORTCUTS_PLAY]?.get() ?: Intent())
            .build()
    }

    private fun shuffle(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, ShortcutsConstants.SHUFFLE)
            .setShortLabel(context.getString(R.string.shortcut_shuffle))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_shuffle))
            .setIntent(intents[NavigationIntent.SHORTCUTS_SHUFFLE]?.get() ?: Intent())
            .build()
    }

    private fun playlistChooser(): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, ShortcutsConstants.PLAYLIST_CHOOSER)
            .setShortLabel(context.getString(R.string.shortcut_playlist_chooser))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_playlist_add))
            .setIntent(intents[NavigationIntent.PLAYLIST_CHOOSER]?.get() ?: Intent())
            .build()
    }

}