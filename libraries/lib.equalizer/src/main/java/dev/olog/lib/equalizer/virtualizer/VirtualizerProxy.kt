package dev.olog.lib.equalizer.virtualizer

import javax.inject.Inject

internal class VirtualizerProxy @Inject constructor(
    private val virtualizer1: IVirtualizerInternal,
    private val virtualizer2: IVirtualizerInternal
) : IVirtualizer {

    private val cache = mutableMapOf<Int, IVirtualizerInternal>()

    private fun retrieveImpl(callerHash: Int): IVirtualizerInternal {
        if (!cache.containsKey(callerHash)) {
            when {
                // check if eq 1 is used, and if not assign it
                !cache.containsValue(virtualizer1) -> cache[callerHash] = virtualizer1
                // check if eq 2 is used, and if not assign it
                !cache.containsValue(virtualizer2) -> cache[callerHash] = virtualizer2
                else -> {
                    // if something goes wrong, fallback to eq 1
                    cache[callerHash] = virtualizer1
                }
            }
        }
        return cache[callerHash]!!
    }

    override fun onAudioSessionIdChanged(callerHash: Int, audioSessionId: Int) {
        retrieveImpl(callerHash).onAudioSessionIdChanged(audioSessionId)
    }

    override fun getStrength(): Int {
        return virtualizer1.getStrength()
    }

    override fun setStrength(value: Int) {
        virtualizer1.setStrength(value)
        virtualizer2.setStrength(value)
    }

    override fun setEnabled(enabled: Boolean) {
        virtualizer1.setEnabled(enabled)
        virtualizer2.setEnabled(enabled)
    }

    override fun onDestroy(callerHash: Int) {
        cache[callerHash]?.onDestroy()
        cache.remove(callerHash)
    }
}