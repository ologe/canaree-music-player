# Release notes 

## 4.0.0
- 🥳🥳 ANIMATIONS 🥳🥳
- Added Floating mini player
- Bug fixes
- Removed tablet support

## 3.3.1
- added dark mode to floating window on Q

## 3.3.0
- Unlock pro features for all users 

## 3.2.2
- Fixed premium not working

## 3.2.1
- ??

## 3.2.0
- libraries update
- crash fixes
- fixed equalizer when using gapless playback

## 3.1.1
- Added link to HAVOC in About section
- Crash fixes

## 3.1.0
- Rollback to old billing implementation (fixed auto-refund)
- Remove stylized image feature

## 3.0.23
- Fixed auto-refund after 3 days of purchasing the app

## 3.0.22
- Fixed ANR when playing a song from file manager or voice assistant
- Added automated translation service, see the About section for more information

## 3.0.21
- Fixed crash on startup

## 3.0.20
- Fixed create equalizer preset crash
- Updated player appearances size 

## 3.0.1.9
- Fixed trial not working as intended
- Updated exo-player to 2.10.4

## 3.0.1.8
- Behavior changes:
    - Repeat all now repeats the current queue only once instead of filling the player mini queue
- Updated player themes size
- Fixed 'classic player controls' setting not working 
- Fixed adaptive color not always working as intended 
- Fixed 'Play' launcher shortcut not working

## 3.0.1.7
- Bug fixes and improvements
- Fixed immediate crash when playing podcast 
- Added tablet/foldable layout support
- Re-introduced Lollipop support
- Added setting to hide podcasts

## 3.0.1.6
- Fixed some image not loading 

## 3.0.1.5
- Fixed crashes
- Fixed some English mistranslations
- Added Greek, Hindi and Vietnamese translation
- Added *localization request* screen + translators credits
- Updated **shuffle behavior**. Shuffle will be enabled automatically only when playing "shuffle all", or by pressing
    the "shuffle button" in the player screen. Otherwise, if you'll click on a song, shuffle mode
    will be disabled automatically. 

## 3.0.1.4
- Crash fixes

## 3.0.1.3
- Fixed image loading cache miss

## 3.0.1.2
- Fixed screen color issues on some devices
- Fixed crashes 

## 3.0.1.1
- Added some missing Czech, Spanish and Portuguese translations
- Fixed blacklist dialog crash

## 3.0.0

- **Internal changes**
    - Migration from rx to coroutines
    - Splitted app in packages
    - Increased min Android version to 23 (Marshmallow) 

- **UI**
    - Overall UI changes
    - Changed `Dark Mode` implementation to follow [Material Design Specification](https://material.io/design/color/dark-theme.html)
        - Only 3 modes:
            - Follow system (Android Q)/Set by Battery Saver(pre Android Q)
            - Light mode 
            - Dark mode
        - Default covers are now colored
        - Too much saturated colors now will be desaturated for a better contrast
    - Added volume controls in all player themes
    - New icon shape available called `cut corner`
    - Merged `Tracks` and `Podcast` bottom navigation items to a single `Library` item
    - Added bottom navigation, sliding panel, toolbar, tab layout and FABs translation Y when 
        scrolling any list
    - Added text horizontal scroll to mini player and player 
    - Added dynamic grid size
    - Renamed `Clean` player appearance to `Glow`
    - Added auto scroll to sync lyrics  
            
- **Audio**
    - Improved `gapless playback` to work without crossfade enabled
            
- **Features**
    - Added `is podcast` override in edit track, edit album and edit artist, to avoid moving manually 
        a track into `Podcast folder on device. When a track is in the Podcast category, last position 
        will be saved when exiting the app or changing podcast.
    - Made offline lyrics sync customizable for every track instead of being global
    - Added `10 band equalizer` to android P and above. On devices pre Android P, the default 5 band 
        equalizer will be used
    - Added changelog link in about screen
    - Added Github link in about screen
    - Now you can opt-in to show a banner AD to enable premium
    - Unlocked some settings customization to all users
    - Added artist and album biography
    - Added `image stylizer`: when editing a track, album and artist you can choose to blend the album 
        art with a painting (additional module has to be downloaded). Thanks to [Fritz](https://www.fritz.ai/). 
      
- **Misc**
    - **Android Q support**
    - When a track title contains `explicit` (case insensitive + parenthesis allowed), an explicit icon
        will be displayed
    - Added animations to floating window
    - Cold/hot start performance improved
    - Overall performance improvements
    - Force to portrait only
    - Removed widget with queue (ugly, bad performance and was causing crashes)
    - When trying to login to LastFm, a network call will be made to check is the credentials are valid
    - Now images will be downloaded from Deezer instead of LastFm because of better quality (1000x1000)
    - Updated community from Google+ (deprecated by Google) to a [subreddit](https://www.reddit.com/r/canaree/)
    - Removed share app  
            
- **Bug fixes**
    - Improved overall stability
    - Android Auto stuck at loading 
    - Playing queue drag and drop + index problems
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