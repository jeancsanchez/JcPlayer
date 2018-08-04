package com.example.jean.jcplayer

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
    fun onPreparedAudio(status: JcStatus)

    /**
     * The audio ends.
     */
    fun onCompletedAudio()

    /**
     * The audio is paused.
     */
    fun onPaused(status: JcStatus)

    /**
     * The audio was paused and user hits play again.
     */
    fun onContinueAudio(status: JcStatus)

    /**
     *  Called when there is an audio playing.
     */
    fun onPlaying(status: JcStatus)

    /**
     * Called when the time of the audio changed.
     */
    fun onTimeChanged(status: JcStatus)


    /**
     * Called when the player stops.
     */
    fun onStopped(status: JcStatus)

    /**
     * Notifies some error.
     * @param throwable The error.
     */
    fun onJcpError(throwable: Throwable)
}