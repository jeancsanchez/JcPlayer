package com.example.jean.jcplayer.service

/**
 * This class represents all the [JcPlayerService] callbacks.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 18/12/17.
 * Jesus loves you.
 */
interface JcpServiceListener {

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
    fun onPlaying()

    /**
     * Called when the time of the audio changed.
     * @param currentTime The current time of the audio.
     */
    fun onTimeChanged(currentTime: Long)

    /**
     * Updates the tile of the current audio.
     * @param title The audio title.
     */
    fun onUpdateTitle(title: String)
}