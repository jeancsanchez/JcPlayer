package com.example.jean.jcplayer;

import android.support.annotation.RawRes;

import java.io.Serializable;

/**
 * Created by jean on 27/06/16.
 */

public class JcAudio implements Serializable {
    private long id;
    private String title;
    private int position;
    private String path;
    private Origin origin;


    public JcAudio(String title, String path, Origin origin){
        // It looks bad
        int randomNumber = path.length() + title.length();

        this.id = randomNumber;
        this.position = randomNumber;
        this.title = title;
        this.path = path;
        this.origin = origin;
    }

    public JcAudio(String title, String path, long id, int position, Origin origin){
        this.id = id;
        this.position = position;
        this.title = title;
        this.path = path;
        this.origin = origin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public static JcAudio createFromRaw(@RawRes int rawId){
        return new JcAudio(String.valueOf(rawId), String.valueOf(rawId), Origin.RAW);
    }

    public static JcAudio createFromRaw(String title, @RawRes int rawId){
        return new JcAudio(title, String.valueOf(rawId), Origin.RAW);
    }

    public static JcAudio createFromAssets(String assetName){
        return new JcAudio(assetName, assetName, Origin.ASSETS);
    }

    public static JcAudio createFromAssets(String title, String assetName){
        return new JcAudio(title, assetName, Origin.ASSETS);
    }

    public static JcAudio createFromURL(String url) {
        return new JcAudio(url, url, Origin.URL);
    }

    public static JcAudio createFromURL(String title, String url) {
        return new JcAudio(title, url, Origin.URL);
    }

    public static JcAudio createFromFilePath(String filePath) {
        return new JcAudio(filePath, filePath, Origin.FILE_PATH);
    }

    public static JcAudio createFromFilePath(String title, String filePath) {
        return new JcAudio(title, filePath, Origin.FILE_PATH);
    }
}