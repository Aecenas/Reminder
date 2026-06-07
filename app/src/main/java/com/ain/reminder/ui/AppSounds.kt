package com.ain.reminder.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.ain.reminder.R

internal enum class SoundCue {
    TapLeaf,
    SwitchLeaf,
    OpenBloom,
    CloseSoft,
    SelectDrop,
    AddSeed,
    SaveGlow,
    DeleteWarn,
    DeleteConfirm,
    HoldWater,
    HoldCancel,
    DoseComplete
}

internal val LocalAppSounds = staticCompositionLocalOf { AppSounds.Silent }

internal class AppSounds private constructor(
    context: Context?,
    private val soundPool: SoundPool?
) {
    private val loadedIds = mutableSetOf<Int>()
    private val sampleIds: Map<SoundCue, Int> = if (context != null && soundPool != null) {
        val appContext = context.applicationContext
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loadedIds += sampleId
        }
        SoundCue.entries.associateWith { cue ->
            soundPool.load(appContext, cue.rawResId, 1)
        }
    } else {
        emptyMap()
    }

    fun play(cue: SoundCue) {
        val pool = soundPool ?: return
        val sampleId = sampleIds[cue] ?: return
        if (sampleId !in loadedIds) return
        pool.play(sampleId, 0.68f, 0.68f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        loadedIds.clear()
    }

    private val SoundCue.rawResId: Int
        get() = when (this) {
            SoundCue.TapLeaf -> R.raw.tap_leaf
            SoundCue.SwitchLeaf -> R.raw.switch_leaf
            SoundCue.OpenBloom -> R.raw.open_bloom
            SoundCue.CloseSoft -> R.raw.close_soft
            SoundCue.SelectDrop -> R.raw.select_drop
            SoundCue.AddSeed -> R.raw.add_seed
            SoundCue.SaveGlow -> R.raw.save_glow
            SoundCue.DeleteWarn -> R.raw.delete_warn
            SoundCue.DeleteConfirm -> R.raw.delete_confirm
            SoundCue.HoldWater -> R.raw.hold_water
            SoundCue.HoldCancel -> R.raw.hold_cancel
            SoundCue.DoseComplete -> R.raw.dose_complete
        }

    companion object {
        val Silent = AppSounds(context = null, soundPool = null)

        fun create(context: Context): AppSounds {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val pool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attributes)
                .build()
            return AppSounds(context, pool)
        }
    }
}

@Composable
internal fun rememberAppSounds(): AppSounds {
    val context = LocalContext.current
    val sounds = remember(context) { AppSounds.create(context) }
    DisposableEffect(sounds) {
        onDispose { sounds.release() }
    }
    return sounds
}
