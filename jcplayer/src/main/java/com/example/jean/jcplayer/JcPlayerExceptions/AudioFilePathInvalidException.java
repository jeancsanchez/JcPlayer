package com.example.jean.jcplayer.JcPlayerExceptions;

/**
 * Invalid audio file path exception.
 * Created by Joielechong on 29 November 2016.
 */

public class AudioFilePathInvalidException extends Exception {
    public AudioFilePathInvalidException(String url){
        super("The file path is not a valid path: " + url +
        "\n" +
        "Have you add File Access Permission?");
    }
}
