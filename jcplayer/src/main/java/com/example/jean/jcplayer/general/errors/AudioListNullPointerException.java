package com.example.jean.jcplayer.general.errors;

/**
 * This is a custom exception thrown when the audio list is null or empty.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 02/08/16.
 * Jesus loves you.
 */
public class AudioListNullPointerException extends NullPointerException {
    public AudioListNullPointerException() {
        super("The playlist is empty or null");
    }
}
