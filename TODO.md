## TODO
- desaturation not work on all components, like tab layout
    - desaturation not removed completely in settings after switching from dark to light mode
- check material card view to view how to draw colored shadow
- check mediastore insert/update/delete on Android Q 

### bug
- big image layout has problems with loading images, and displaying text (unknown cause)
- player not scrolling properly   

## features
- use don't keep activities and add try to restore previous state
- add in search fragment a chip group that let you choose search categories
- add add to favorite in android auto
- check if can use custom icons in android auto
- custom preset in equalizer
- add a flag in update item that sets the track as podcast // overriding IS_PODCAST column
- made lyrics sync for every track
- settings to blur lockscreen image


## DONE
- fix android auto not working
- fix player swipe
- re-enable remix and explicit icons? try with precomputed text API -> only explicit, can't find a remix icon
- since now album and artist has wiki, show it in detail

## Maybe
- highlight playing song in every list
- limit shuffle all to N (something like 500), and the let do the shuffle
  algoritm  the rest when pressing shuffle again (like prioritize track not played first,
  then added the rest)
- try double tap to forward/replay?
- whitelist
- allow user to sync lyrics to song