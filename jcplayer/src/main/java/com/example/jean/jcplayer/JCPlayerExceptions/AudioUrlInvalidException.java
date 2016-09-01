package com.example.jean.jcplayer.JCPlayerExceptions;

/**
 * Created by jean on 01/09/16.
 */

public class AudioUrlInvalidException extends Exception{
    public AudioUrlInvalidException(String url){
        super("The url does not appear valid: " + url);
    }
}
