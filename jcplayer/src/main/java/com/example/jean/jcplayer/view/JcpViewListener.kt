package com.example.jean.jcplayer.view

import com.example.jean.jcplayer.general.JcStatus

/**
 * This class represents all the [JcPlayerView] callbacks.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 18/12/17.
 * Jesus loves you.
 */
interface JcpViewListener {

    /**
     * Called when player is paused.
     * @param jcStatus [JcStatus] status.
     */
    fun onPausedStatus(jcStatus: JcStatus)

    /**
     * Called when the player was stopped and user hits play again.
     * @param jcStatus [JcStatus] status.
     */
    fun onContinueAudioStatus(jcStatus: JcStatus)

    /**
     * Audio is playing.
     * @param jcStatus [JcStatus] status.
     */
    fun onPlayingStatus(jcStatus: JcStatus)

    /**
     * Audio time changed.
     * @param jcStatus [JcStatus] status.
     */
    fun onTimeChangedStatus(jcStatus: JcStatus)

    /**
     * Called when the audio ends.
     * @param jcStatus [JcStatus] status.
     */
    fun onCompletedAudioStatus(jcStatus: JcStatus)

    /**
     * The audio is ready to be played.
     * @param jcStatus [JcStatus] status.
     */
    fun onPreparedAudioStatus(jcStatus: JcStatus)
}