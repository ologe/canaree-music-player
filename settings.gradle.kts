include(":app")

// domain
include(":core")
include(":domain")

// flavors
include(":flavor-full")

// libraries
include(":lib.media")
include(":lib.image-loader")
include(":lib.offline-lyrics")
include(":lib.equalizer")
include(":lib.network")
include(":lib.analytics")
include(":lib.audio-tagger")

// features
include(":navigation")
include(":feature-presentation-base")
include(":feature-service-music")
include(":feature-service-floating")
include(":feature-app-shortcuts")
include(":feature-library")
include(":feature-search")
include(":feature-detail")
include(":feature-player")
include(":feature-player-mini")
include(":feature-queue")
include(":feature-settings")
include(":feature-about")
include(":feature-onboarding")
include(":feature-equalizer")
include(":feature-edit")
include(":presentation")

// data
include(":data")
include(":data-spotify")

// shared
include(":shared")
include(":shared-android")
include(":prefs-keys")
include(":lint")
include(":test-shared")

// remove
include(":intents")


val extensionAware = gradle as ExtensionAware
extensionAware.extra["exoplayerRoot"] = "/Users/eugeniuolog/AndroidStudioProjects/ExoPlayer"
extensionAware.extra["exoplayerModulePrefix"] = "exoplayer-"
apply(from = File(extensionAware.extra["exoplayerRoot"] as String, "core_settings_min.gradle"))
