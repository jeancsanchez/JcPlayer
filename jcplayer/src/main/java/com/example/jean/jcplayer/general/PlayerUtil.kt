package com.example.jean.jcplayer.general

/**
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 02/06/18.
 * Jesus loves you.
 */
object PlayerUtil {

    /**
     * Transform the current audio time to string to be displayed on player.
     * @param currentPosition The current audio duration.
     */
    @JvmStatic
    fun toTimeSongString(currentPosition: Int): String {
        val aux = currentPosition / 1000
        val minutes = (aux / 60)
        val seconds = (aux % 60)
        val sMinutes = if (minutes < 10) "0$minutes" else minutes.toString()
        val sSeconds = if (seconds < 10) "0$seconds" else seconds.toString()

        return "$sMinutes:$sSeconds"
    }
}