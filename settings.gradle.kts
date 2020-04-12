include(":app")

// domain
include(":core")
include(":domain")

// flavors
include(":flavors:full")
include(":flavors:lite")

// libraries
include(":libraries:media")
include(":libraries:image-loader")
include(":libraries:offline-lyrics")
include(":libraries:equalizer")
include(":libraries:network")
include(":libraries:analytics")
include(":libraries:audio-tagger")

// features
include(":navigation")
include(":features:presentation-base")
include(":features:service-music")
include(":features:service-floating")
include(":features:app-shortcuts")
include(":features:library")
include(":features:search")
include(":features:detail")
include(":features:player")
include(":features:player-mini")
include(":features:queue")
include(":features:settings")
include(":features:about")
include(":features:onboarding")
include(":features:equalizer")
include(":features:edit")
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
