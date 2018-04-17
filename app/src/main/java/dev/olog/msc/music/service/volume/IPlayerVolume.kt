package dev.olog.msc.music.service.volume

interface IPlayerVolume {

    var listener: IPlayerVolume.Listener?

    fun getVolume(): Float
    fun normal(): Float
    fun ducked(): Float

    interface Listener {
        fun onVolumeChanged(volume: Float)
    }

}