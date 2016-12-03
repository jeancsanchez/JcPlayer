package com.example.jean.jcplayer.JcPlayerExceptions;

/**
 * Invalid raw resource file id exception.
 * Created by Joielechong on 29 November 2016.
 */
public class AudioRawInvalidException extends Exception {
    public AudioRawInvalidException(String rawId){
        super("Not a valid raw file id: " + rawId);
    }
}
