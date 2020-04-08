include(":app")

include(":core")


include(":domain")
include(":presentation")
include(":shared-android")
include(":data")
include(":lib.image-loader")
include(":prefs-keys")
include(":jaudiotagger")
include(":lib.media")
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
