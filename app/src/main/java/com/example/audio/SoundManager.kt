package com.example.audio

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundManager {
  private var isMuted = false
  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (e: Exception) {
      toneGenerator = null
    }
  }

  fun toggleMute(): Boolean {
    isMuted = !isMuted
    return isMuted
  }

  fun isAudioMuted(): Boolean = isMuted

  fun playKickSound() {
    if (isMuted) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
      } catch (e: Exception) {
        // safe ignore
      }
    }
  }

  fun playImpactSound() {
    if (isMuted) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 120)
      } catch (e: Exception) {
        // safe ignore
      }
    }
  }

  fun playBlockSound() {
    if (isMuted) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
      } catch (e: Exception) {
        // safe ignore
      }
    }
  }

  fun playKihapSound() {
    if (isMuted) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 150)
      } catch (e: Exception) {
        // safe ignore
      }
    }
  }

  fun playSuccessFanfare() {
    if (isMuted) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 250)
      } catch (e: Exception) {
        // safe ignore
      }
    }
  }

  fun playGong() {
    if (isMuted) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_NETWORK_USA_RINGBACK, 300)
      } catch (e: Exception) {
        // safe ignore
      }
    }
  }

  fun release() {
    try {
      toneGenerator?.release()
      toneGenerator = null
    } catch (e: Exception) {
      // safe ignore
    }
  }
}
