package com.example.jean.jcplayer.general.errors;

/**
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 01/09/16.
 * Jesus loves you.
 */
public class AudioUrlInvalidException extends IllegalStateException {
    public AudioUrlInvalidException(String url) {
        super("The url does not appear valid: " + url);
    }
}
