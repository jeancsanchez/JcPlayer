package com.example.jean.jcplayer.JCPlayerExceptions;

/**
 * Created by jean on 02/08/16.
 */

public class AudioListNullPointerException extends Exception {
    public AudioListNullPointerException(){
        super("The playlist is empty or null");
    }
}
