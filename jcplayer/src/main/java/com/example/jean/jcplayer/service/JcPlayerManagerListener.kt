package com.example.jean.jcplayer.service

import com.example.jean.jcplayer.general.JcStatus

/**
 * This class represents all the [JcPlayerService] callbacks.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 18/12/17.
 * Jesus loves you.
 */
interface JcPlayerManagerListener {

    /**
     * Prepares the new audio.
     * @param audioName The audio name.
     * @param duration The audio duration.
     */
    fun onPreparedAudio(audioName: String, duration: Int)

    /**
     * The audio ends.
     */
    fun onCompletedAudio()

    /**
     * The audio is paused.
     */
    fun onPaused()

    /**
     * The audio was paused and user hits play again.
     */
    fun onContinueAudio()

    /**
     *  Called when there is an audio playing.
     */
    fun onPlaying(status: JcStatus)

    /**
     * Called when the time of the audio changed.
     */
    fun onTimeChanged(status: JcStatus)

    /**
     * Updates the tile of the current audio.
     * @param title The audio title.
     */
    fun onUpdateTitle(title: String)

    /**
     * Notifies some error.
     * @param throwable The error.
     */
    fun onJcpError(throwable: Throwable)
}