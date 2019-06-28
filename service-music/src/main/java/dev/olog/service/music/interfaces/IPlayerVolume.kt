package dev.olog.service.music.interfaces

interface IPlayerVolume {

    var listener: Listener?

    fun getVolume(): Float
    fun normal(): Float
    fun ducked(): Float

    interface Listener {
        fun onVolumeChanged(volume: Float)
    }

}