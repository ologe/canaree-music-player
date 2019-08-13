## Player Architecture

<img src="https://github.com/ologe/canaree-music-player/blob/master/docs/images/player_architecture.jpg">

<br>

### `IPlayer`
- Exposes playback API to other components

### `PlayerImpl`
- Delegates playback calls to some `IPlayerDelegate<PlayerMediaEntity>` implementation obtained via DI
 that will handle the actual playback 
- Notify player state to other components (play, pause, skip, ..)
- Handles audio focus
- Handles changes in volume

### `IPlayerDelegate<T>`
- Abstraction to allow easier changes in playback implementation, at the moment this class is implemented
    by `CrossFadePlayerSwitcher` that implements crossfade 

<br>

## Crossfade and Gapless

### `CrossFadePlayerSwitcher`
- Internally has 2 `CrossFadePlayer`
- Delegates playback calls to the right player `CrossFadePlayer`

### `CrossFadePlayer`
- Actual crossfade implementation, fades out the current song and fades in next
- Fakes gapless by using crossfade, that forces next song preload. Volume is kept quite high
    to avoid gapless distorsion
- Delegates playback calls to `AbsPlayer`

### `AbsPlayer<T>`
- Handles the actual playback