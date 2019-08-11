# Release notes

## 3.0.0

- **Internal changes**
    - Migration from rx to coroutines
    - Splitted app in packages

- **UI**
    - Overall ui changes
    - Changed dark mode implementation to follow [Material Design Specification](https://material.io/design/color/dark-theme.html)
        - Only 3 dark mode modes:
            - Follow system (Android Q)/Set by Battery Saver(pre Android Q)
            - Light mode 
            - Dark mode
        - Dark mode default covers are now colored
        - Too much saturated colors now will be desaturated for a better contrast
    - Added volume controls in all player themes
    - New icon shape available
        - Cur corner
    - Merged `Tracks` and `Podcast` bottom navigation items to a single `Library` item
    - Added bottom navigation, sliding panel, toolbar, tab layout and FABs translation Y when 
        scrolling any list
    - Added text horizontal scroll to mini player and player 
    - Added dynamic grid size
            
- **Features**
    - Added `is podcast` override in edit track, edit album and edit artist
    - Made offline lyrics sync customizable for for track instead of being global
    - Added 10 band equalizer to android P and above, on pre Android P devices, the default 5 band 
        equalizer will be used
    - Added changelog in about screen
    - Now you can opt-in to show a banner AD to enable premium
    - Unlocked some setting customization to all users
    - Added artist and album biography
    - Added image stylizer: when editing a track, album and artist you can choose to blend the album 
        art with a painting (additional module has to be downloaded). Thanks to [Fritz](https://www.fritz.ai/). 
      
- **Misc**
    - Android Q support
    - When title contains `explicit` (case insensitive + parenthesis allowed), an explicit icon
        will be displayed
    - Added animations to floating window
    - Cold/hot start performance increased
    - Overall performance increase
    - Force to portrait only
    - Removed widget with queue (ugly, bad performance and causing crashes)
    - When trying to login to LastFm, a network call will be made to check is inserted
        credentials are valid
    - Now images will be downloaded from Deezer instead of LastFm because of better quality (1000x1000)
    - Updated community from Google+ (deprecated by Google) to a [subreddit](https://www.reddit.com/r/canaree/)
    - Removed share app  
            
- **Bug fixes**
    - Improved overall stability
    - Android Auto stuck at loading 
    - Fixed playing queue drag and drop + index problems
    - Overall drag and drop fix
    - Fixed swipeable view swipe not working as intended  

## 2.1.1
- Fixed all tracks sort order by date

## 2.0.12
- Removed disabling day night from base activity
- Removed unused dagger injection in splash
- Fixed dark theme in library categories

## 2.0.11
- Removed unused code


## 2.0.10
- Try to fix ForegroundImageView crash
- Spanish typo fix

## 2.0.9
- Added firebase invites analytics

## 2.0.8
- Song cursor mapper folder crash fix

## 2.0.7
- Added spanish language

## 2.0.6
- Improved analytics:
    - tracks current screen
- Added perf monitoring
- Added in-app message
- Added website deep-linking
- Added small implementation for google personal search indexing
- Added invite friends

## 2.0.5
- Fix adaptive color Image presenter crash
- Fixed null string crash cursor mapper
- Added keep rule to TextInputEditText in proguard

## 2.0.4
- Fixed color accent crash
- Fixed crash on repeat mode changed
- Added message when starting updating album/artist
- Fix playback speed menu typo
- Fixed square image shape on player
- Added option to show lockscreen album art
- Added option to ignore media store thumbnails
- Removed edit item ripple on click
- Fixed low quality images
- Fixed download track image automatically
- Fixed edit track not showing properly

## 2.0.3

- Migration to AndroidX
- Added black theme to intro
- Overall fixes to dark/black theme
- Fixed FAB not always working in offline lyrics/floating window
- Added save playing queue as playlist
- Fixed delete podcast playlist popup
- Preferences reorder
- Changed library categories landscape behavior
- Added option to enable or disable adaptive colors
- Added immersive mode
- Added search podcast
- Added FAB to show keyboard
- Fixed notch
- Create playlist not adding tracks on creation
- Buggy swipe and drag in playing queue
- Removed corner radius to big image theme
- Albums not showing in detail
- Playing queue not handling correctly duplicated songs


## 2.0.2
- Added mini player player theme
- Added support to do not download album image that has same app as folder (finish to implement)
- Removed preset chooser popup


## 2.0.1
- Tap tutorial draw out of screen border
- Fixed rounded search theme colors
- Added show/hide podcast preference
- Added buy premium to main popup
- Fixed swipe animation
- Set jaudiotagger encoding to UTF-8


## 2.0.0
- Changed app name to Canaree (like Canary)
- Overall redesign
- Clean theme redesign
- Edit item redesign
- Floating window redesign
- Offline lyrics redesign
- Fix crash when cleaning app data
- Changed swipe animation
- Fixed adaptive color
- Fixed skip to next behavior when last song
- Improved search: added folders, genres and playlists
- Enhanced audio focus, now resumes music after playing a short video
- Added play from file manager
- Added 'save default folder' to folder hierarchy view
- Fixed missing albums
- Improved podcast support
- Added RECENTLY PLAYED and NEW albums and artists
- Fixed left space visual bug in settings
- Added filter songs in detail view
- Added change playback speed
- Fixed sorting when unknown album/artist
- Fixed swipe to delete crash in playing queue