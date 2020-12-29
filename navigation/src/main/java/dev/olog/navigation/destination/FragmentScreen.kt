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

    EDIT_TRACK("$PREFIX.edit.track"),
    EDIT_ALBUM("$PREFIX.edit.album"),
    EDIT_ARTIST("$PREFIX.edit.artist"),

    ABOUT("$PREFIX.about"),
    SPECIAL_THANKS("$PREFIX.special.thanks"),
    LOCALIZATION("$PREFIX.localization"),
    LICENSE("$PREFIX.license");

    companion object {
        const val OWNERSHIP = PREFIX
    }

}