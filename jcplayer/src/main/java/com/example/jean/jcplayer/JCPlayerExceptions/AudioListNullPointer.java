package com.example.jean.jcplayer.JCPlayerExceptions;

/**
 * Created by jean on 02/08/16.
 */

public class AudioListNullPointer extends Exception {
    public AudioListNullPointer(){
        super("The playlist is empty or null");
    }
}
