## TODO
- check mediastore insert/update/delete on Android Q
- change grid span
- swap last fm artist with deezer (https://developers.deezer.com/api/artist) 
- re add podcast auto playlist
- add alpha to toolbar image button in detail fragment when scrolling up
- fix stilyze image (dynamic module crashes)
- add keep resource file in :feature-stylizer that keeps module title, and all the images
- fix edit album & edit artist
- reduce method count (opening dex file take a lot of time on on app startup)

## TOOD for methoud count reducing
- remove last fm binding ~700 methods
- removed jvmoverlaod when possibile
- add jvm static to companion classes

### bug
- big image layout has problems with loading images 
    (has problem on my device with android P beta, on emulator works fine, check on other phones)
- floating window play button in search lyrics not always working 
- audio is not paused on some devices during a call (Huawei P30 lite)
- after creating 'new playlist' from + button, bottom navigation disappears
- detail is not displaying corretly track number
- figure out how to change preference switch track disabled color
- on opening the app, the first time visiting settings in dark mode, the disable color doesn't work
- FlowLiveData in :data is not working as intended

## features
- add to favorite in android auto
- check if can use custom icons in android auto
- custom preset in equalizer
- made lyrics sync for every track
- settings to blur lockscreen image


## DONE
- fix android auto not working
- fix player swipe
- re-enable remix and explicit icons? try with precomputed text API -> only explicit, can't find a remix icon
- since now album and artist has wiki, show it in detail
- added cut corner image shape
- added 10 band equalizer to android P and above
- added custom eq preset to all versions
- added check last fm authentication
- add custom equalizer preset
- fix snackbar (buy premium) in setting fragment
- check if show album art on lockscreen works (working on AOSP)
- enable premium with ads??

## Maybe
- try double tap to forward/replay?
- whitelist
- made sleep timer top and bottom curved