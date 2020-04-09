include(":app")

include(":core")
include(":domain")

include(":lib.media")
include(":lib.image-loader")
include(":lib.offline-lyrics")
include(":lib.equalizer")

include(":navigation")
include(":feature-presentation-base")
include(":feature-service-music")
include(":feature-service-floating")
include(":feature-app-shortcuts")
include(":feature-library")


include(":data")
include(":data-spotify")
include(":lib.network")

include(":presentation")


include(":shared")
include(":shared-android")
include(":prefs-keys")

include(":analytics")

include(":lint")
include(":test-shared")

// remove
include(":intents")


val extensionAware = gradle as ExtensionAware
extensionAware.extra["exoplayerRoot"] = "/Users/eugeniuolog/AndroidStudioProjects/ExoPlayer"
extensionAware.extra["exoplayerModulePrefix"] = "exoplayer-"
apply(from = File(extensionAware.extra["exoplayerRoot"] as String, "core_settings_min.gradle"))
