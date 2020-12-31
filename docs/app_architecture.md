## App Architecture

<img src="https://github.com/ologe/canaree-music-player/blob/master/docs/images/app_architecture.jpg">

<br>

### Legend
- Every rectangle is an Android `:module`
- Every vertical line delimits a `logic layer`
- To avoid arrows hell, only some important dependencies are highlighted
- A `:module` in a `layer`:
    - can't depend on other modules in the same layer (except `shared layer`)
    - can depend on any module in any layer on it's upper right, or on the layer below

## Modules

#### `:app`
- Must depend on almost every `:module` in order to build the apk
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/app/build.gradle)

#### `:domain`
- Contains gateway that allow decoupled communication between `:data` and other modules
- Contains common entities
- Contains interactors (business use-cases)
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/domain/build.gradle)

#### `:service-music`
- Self explanatory
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/service-music/build.gradle)

#### `:service-floating`
- Self explanatory
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/service-floating/build.gradle)

#### `:libraries:image-provider`
- Handles all image loading (from local storage and network) and caching
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/libraries/image-provider/build.gradle)

#### `:data`
- Repositories implementation
- Makes network calls and caching
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/data/build.gradle)

## Libs

#### `:libraries:media`
- Provides a reactive API to connect to `:service-music`
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/libraries/media/build.gradle)

#### `:libraries:equalizer`
- Equalizer, BassBoos and Virtualizer implementation for different API level
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/libraries/equalizer/build.gradle)

#### `:feature_stylize`
- Removed

#### `:libraries:offline-lyrics`
- Provides an API to read offline lyrics, saved on device on from track metadata
- Supports `.lrc` file format for synced lyrics
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/libraries/offline-lyrics/build.gradle)

#### `:libraries:jaudiotagger`
- Allows to read and update tracks metadata
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/libraries/jaudiotagger/build.gradle)

## Utils

#### `:shared:prefs-keys`
- Contains shared preferences keys, using a separate module for communication between modules
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/prefs-keys/build.gradle)

#### `:shared:jvm`
- Shared pure java/kotlin utilities
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/shared/build.gradle)

#### `:shared:android`
- Self explanatory
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/shared-android/build.gradle) 

#### `:shared:widgets`
- Self explanatory
- [build.gradle](https://github.com/ologe/canaree-music-player/blob/master/shared-widgets/build.gradle)
