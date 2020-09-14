package dev.olog.navigation.screens

private const val PREFIX = "dev.olog"

// TODO assert unique tag
enum class FragmentScreen(val tag: String) {
    HOME("$PREFIX.home"),
    LIBRARY("$PREFIX.library"),
    PLAYLIST("$PREFIX.playlist"),
    SEARCH("$PREFIX.search"),
    QUEUE("$PREFIX.queue"),
    LIBRARY_CHOOSER("$PREFIX.library.chooser"),
    LIBRARY_SPAN("$PREFIX.library.span"),

    DETAIL("$PREFIX.detail"),
    PLAYER("$PREFIX.player"),
    PLAYER_MINI("$PREFIX.player.mini"),
    SETTINGS("$PREFIX.settings"),
    ABOUT("$PREFIX.about"),
    ONBOARDING("$PREFIX.onboarding"),
    EQUALIZER("$PREFIX.equalizer"),
    EDIT_TRACK("$PREFIX.edit.track"),
    EDIT_ALBUM("$PREFIX.edit.album"),
    EDIT_ARTIST("$PREFIX.edit.artist");

    companion object {
        const val OWNERSHIP = PREFIX
    }

}