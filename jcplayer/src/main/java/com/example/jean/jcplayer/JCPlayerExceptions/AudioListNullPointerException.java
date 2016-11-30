package com.example.jean.jcplayer.JcPlayerExceptions;

/**
 * Created by jean on 02/08/16.
 */

public class AudioListNullPointerException extends NullPointerException {
    public AudioListNullPointerException(){
        super("The playlist is empty or null");
    }
}
