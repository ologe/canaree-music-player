## TODO
- try app on all API level
- test on foldable
- TODO check spanish and portuguese translation 

## TODO for methoud count reducing
- reduce method count (opening dex file take a lot of time on on app startup)
- remove last fm binding ~700 methods

### strange bug 
- audio is not paused on some devices during a call (Huawei P30 lite)

### bug
- merged images don't update after content change (folders and genres)
- in ImageRetrieverRepository, when retrieving album image, if has same name as folder, extract the real 
    album with jaudio tagger instead of skipping download
- recent search song hasn't ripple on click
-  recent search song don't display album, just a dot

### urgent bug
- crashes when there are no tracks
- sometimes when opening a fragment, it opens only a blank screen (fragment lib bug?)
- not always loading embedded image

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
- add lyrics sync for every track
- fix snackbar (buy premium) in setting fragment
- check if show album art on lockscreen works (working on AOSP)
- enable premium with ads
- swap last fm artist with deezer (https://developers.deezer.com/api/artist) 


## features


## Maybe
- settings to blur lockscreen image
- try double tap to forward/replay?
- whitelist
- made sleep timer top and bottom curved