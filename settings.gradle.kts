include(":app")
include(":injection")
include(":domain")
include(":presentation")
include(":shared-android")
include(":data")
include(":image-provider")
include(":prefs-keys")
include(":jaudiotagger")
include(":media")
include(":app-shortcuts")
include(":service-music")
include(":service-floating")
include(":offline-lyrics")
include(":test-shared")
include(":intents")
include(":shared")
include(":shared-widgets")
include(":equalizer")
include(":analytics")
include(":lint")
include(":data-spotify")
include(":data-shared")


val extensionAware = gradle as ExtensionAware
extensionAware.extra["exoplayerRoot"] = "/Users/eugeniuolog/AndroidStudioProjects/ExoPlayer"
extensionAware.extra["exoplayerModulePrefix"] = "exoplayer-"
apply(from = File(extensionAware.extra["exoplayerRoot"] as String, "core_settings_min.gradle"))
