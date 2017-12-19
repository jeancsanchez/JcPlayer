package com.example.jean.jcplayer.general

import com.example.jean.jcplayer.JcAudio

/**
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 18/12/17.
 * Jesus loves you.
 */
interface OnInvalidPathListener {

    /**
     * Audio path error listener.
     * @param jcAudio The wrong audio.
     */
    fun onPathError(jcAudio: JcAudio)
}