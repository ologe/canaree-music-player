# Canaree
### Android music player

Is a complete music player available in the [Play Store](https://play.google.com/store/apps/details?id=dev.olog.msc) 

|               |               |               | 
| ------------- | ------------- | ------------- |
| ![image1](https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-28-235818.png)  | ![image2](https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-29-001417.png) | ![image3](https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-29-002256.png) | 


In order to compile you need to do the following things 
* Clone [ExoPlayer](https://github.com/google/ExoPlayer)
* Set the path 'gradle.ext.exoplayerRoot' to your local ExoPlayer in 'settings.gradle'
* One of the following options:
1) Copy https://github.com/ologe/canaree-music-player/blob/master/exoplayer-gradle/core_settings_min.gradle to your local ExoPlayer root folder
2) In the 'settings.gradle' change **apply from: new File(gradle.ext.exoplayerRoot, 'core_settings_min.gradle')** to **apply from: new File(gradle.ext.exoplayerRoot, 'core_settings.gradle')**

To support FLAC, FFMPEG and OPUS formats to you need to compile manually the corresponding ExoPlayer extensions using NDK-r15c or older, newer version of NDK are not supported.
* https://github.com/google/ExoPlayer/tree/release-v2/extensions/ffmpeg
* https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac
* https://github.com/google/ExoPlayer/tree/release-v2/extensions/opus
