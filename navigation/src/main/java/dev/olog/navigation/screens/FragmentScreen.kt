package dev.olog.navigation.screens

private const val PREFIX = "dev.olog"

enum class FragmentScreen(val tag: String) {
    HOME("$PREFIX.home"),

    TRACKS("$PREFIX.tracks"),
    PLAYLISTS("$PREFIX.playlists"),
    ALBUMS("$PREFIX.albums"),
    ARTISTS("$PREFIX.artists"),
    GENRES("$PREFIX.genres"),
    FOLDERS("$PREFIX.folders"),
    FOLDERS_NORMAL("$PREFIX.folders.normal"),
    FOLDERS_TREE("$PREFIX.folders.tree"),

    SEARCH("$PREFIX.search"),
    QUEUE("$PREFIX.queue"),

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