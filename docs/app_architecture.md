## App Architecture

<img src="https://github.com/ologe/canaree-music-player/blob/master/docs/images/app_architecture.jpg">

<br>

### Legend
- Every rectangle is an Android `:module`
- Every vertical line delimits a `logical layer`
- To avoid arrows hell, only some important dependencies is highlighted
- A `:module` in a `layer`:
    - can't depend on other modules in the same layer (except `shared layer`)
    - can depend on any module in any layer on it's upper right, or on the layer below

## Modules

#### `:app`
- Must depend on almost every `:module` in order to build the apk

#### `:core`
- Contains gateway that allow decoupled communication between `:data` and other modules
- Contains common entities
- Contains interactors (business use-cases)

#### `:presentation`, `:service-music`, `:service-floating`
- Self explanatory

#### `:image-provider`
- Handles all image loading (from local storage and network) and caching

#### `:injection`
- Creates dagger shared core component
- Binds `:core` gateways with `:data` implementations 

#### `:data`
- Repositories implementation
- Makes network calls and caching


## Libs

#### `:media`
- Provides a reactive API to connect to `:service-music`

#### `:equalizer`
- Equalizer, BassBoos and Virtualizer implementation for different API level

#### `:feature_stylize`
- On-demand dynamic module
- Used from image blending/stylize

#### `:offline-lyrics`
- Provides an API to read offline lyrics, saved on device on from track metadata
- Supports `.lrc` file format for synced lyrics 

#### `:jaudiotagger`
- Allows to read and update tracks metadata 

## Utils

#### `:intents`
- Contains common actions and constants for communication between modules

#### `:prefs-keys`
- Contains shared preferences keys, using a separate module for communication between modules

#### `:shared`
- Shared pure java/kotlin utilities

#### `:shared-android`
- Self explanatory 

#### `:shared-widgets`
- Self explanatory