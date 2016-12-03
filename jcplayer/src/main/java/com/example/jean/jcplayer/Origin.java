package com.example.jean.jcplayer;

/**
 * This is the origin of Audio file.
 * Created by Joielechong on 28 November 2016.
 */
public enum Origin {
  URL, // url like http:/www.example.com/sample.mp3
  RAW, // From raw resource folder
  ASSETS, // From asset folder
  FILE_PATH // From file in device path
}
