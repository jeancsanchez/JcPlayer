package com.example.jean.jcplayer.general.errors;

/**
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 01/09/16.
 * Jesus loves you.
 */
public class UninitializedPlaylistException extends IllegalStateException {
    public UninitializedPlaylistException() {
        super("The playlist was not initialized. You must to call to initPlaylist method before.");
    }
}
