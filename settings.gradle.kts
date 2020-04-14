include(":app")

// domain
include(":core")
include(":domain")

// flavors
include(":flavors:flavor-full")
include(":flavors:flavor-lite")

// libraries
include(":libraries:lib-media")
include(":libraries:lib-image-loader")
include(":libraries:lib-offline-lyrics")
include(":libraries:lib-equalizer")
include(":libraries:lib-network")
include(":libraries:lib-analytics")
include(":libraries:lib-audio-tagger")

// features
include(":navigation")
include(":features:feature-presentation-base")
include(":features:feature-service-music")
include(":features:feature-service-floating")
include(":features:feature-app-shortcuts")
include(":features:feature-library")
include(":features:feature-search")
include(":features:feature-detail")
include(":features:feature-player")
include(":features:feature-player-mini")
include(":features:feature-queue")
include(":features:feature-settings")
include(":features:feature-about")
include(":features:feature-onboarding")
include(":features:feature-equalizer")
include(":features:feature-edit")
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
