package dev.olog.lib.equalizer.bassboost

import javax.inject.Inject

internal class BassBoostProxy @Inject constructor(
    private val bassBoost1: IBassBoostInternal,
    private val bassBoost2: IBassBoostInternal
) : IBassBoost {

    private val cache = mutableMapOf<Int, IBassBoostInternal>()

    private fun retrieveImpl(callerHash: Int): IBassBoostInternal {
        if (!cache.containsKey(callerHash)) {
            when {
                // check if eq 1 is used, and if not assign it
                !cache.containsValue(bassBoost1) -> cache[callerHash] = bassBoost1
                // check if eq 2 is used, and if not assign it
                !cache.containsValue(bassBoost2) -> cache[callerHash] = bassBoost2
                else -> {
                    // if something goes wrong, fallback to eq 1
                    cache[callerHash] = bassBoost1
                }
            }
        }
        return cache[callerHash]!!
    }

    override fun onAudioSessionIdChanged(callerHash: Int, audioSessionId: Int) {
        retrieveImpl(callerHash).onAudioSessionIdChanged(audioSessionId)
    }

    override fun getStrength(): Int {
        return bassBoost1.getStrength()
    }

    override fun setStrength(value: Int) {
        bassBoost1.setStrength(value)
        bassBoost2.setStrength(value)
    }

    override fun setEnabled(enabled: Boolean) {
        bassBoost1.setEnabled(enabled)
        bassBoost2.setEnabled(enabled)
    }

    override fun onDestroy(callerHash: Int) {
        cache[callerHash]?.onDestroy()
        cache.remove(callerHash)
    }
}