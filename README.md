# Canaree
### Android music player

Is a complete music player available in the [Play Store](https://play.google.com/store/apps/details?id=dev.olog.msc) 

|               |               |               | 
| ------------- | ------------- | ------------- |
| ![image1](https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-28-235818.png)  | ![image2](https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-29-001417.png) | ![image3](https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-29-002256.png) | 


In order to compile you need to do the following things 
* Clone [ExoPlayer](https://github.com/google/ExoPlayer)
* Set the path 'gradle.ext.exoplayerRoot' to your local ExoPlayer in 'settings.gradle'

To support FLAC, FFMPEG and OPUS formats to you need to compile the corresponding ExoPlayer extensions using NDK-r15c or older, the newer version of NDK are not supported
