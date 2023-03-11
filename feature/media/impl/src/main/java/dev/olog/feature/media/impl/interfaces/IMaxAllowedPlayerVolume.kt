package dev.olog.feature.media.impl.interfaces

interface IMaxAllowedPlayerVolume {

    var listener: Listener?

    /**
     * Range from 0..1
     */
    fun getMaxAllowedVolume(): Float
    /**
     * Range from 0..1
     */
    fun normal(): Float
    /**
     * Range from 0..1
     */
    fun ducked(): Float

    interface Listener {
        fun onMaxAllowedVolumeChanged(volume: Float)
    }

}