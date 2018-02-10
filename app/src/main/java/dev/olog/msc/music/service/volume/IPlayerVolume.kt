package dev.olog.msc.music.service.volume

interface IPlayerVolume {

    var listener: IPlayerVolume.Listener?

    fun normal(): Float
    fun ducked(): Float

    interface Listener {
        fun onVolumeChanged(volume: Float)
    }

}