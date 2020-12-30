package dev.olog.navigation.destination

private const val PREFIX = "dev.olog"

enum class FragmentScreen(val tag: String) {
    LIBRARY_TRACKS("$PREFIX.library.tracks"),
    LIBRARY_PODCASTS("$PREFIX.library.podcasts"),
    SEARCH("$PREFIX.search"),
    QUEUE("$PREFIX.queue"),

    LIBRARY_PREFS("$PREFIX.library.prefs"),

    ONBOARDING("$PREFIX.onboarding"),
    DETAIL("$PREFIX.detail"),
    RELATED_ARTISTS("$PREFIX.related.artists"),
    RECENTLY_ADDED("$PREFIX.recently.added"),

    PLAYER("$PREFIX.player"),
    PLAYER_MINI("$PREFIX.player.mini"),

    EQUALIZER("$PREFIX.equalizer"),
    SETTINGS("$PREFIX.settings"),
    SLEEP_TIMER("$PREFIX.sleep.timer"),
    OFFLINE_LYRICS("$PREFIX.offline.lyrics"),
    CREATE_PLAYLIST("$PREFIX.create.playlist"),
    BLACKLIST("$PREFIX.blacklist"),

    EDIT_TRACK("$PREFIX.edit.track"),
    EDIT_ALBUM("$PREFIX.edit.album"),
    EDIT_ARTIST("$PREFIX.edit.artist"),

    DIALOG_DELETE("$PREFIX.dialog.delete"),
    DIALOG_ADD_FAVORITE("$PREFIX.dialog.add.favorite"),
    DIALOG_PLAY_LATER("$PREFIX.dialog.play.later"),
    DIALOG_PLAY_NEXT("$PREFIX.dialog.play.next"),
    DIALOG_PLAYLIST_CLEAR("$PREFIX.dialog.playlist.clear"),
    DIALOG_PLAYLIST_CREATE("$PREFIX.dialog.playlist.create"),
    DIALOG_PLAYLIST_REMOVE_DUPLICATES("$PREFIX.dialog.playlist.remove.duplicates"),
    DIALOG_PLAYLIST_RENAME("$PREFIX.dialog.playlist.rename"),
    DIALOG_RINGTONE("$PREFIX.dialog.ringtone"),

    ABOUT("$PREFIX.about"),
    SPECIAL_THANKS("$PREFIX.special.thanks"),
    LOCALIZATION("$PREFIX.localization"),
    LICENSE("$PREFIX.license");

    companion object {
        const val OWNERSHIP = PREFIX
    }

}