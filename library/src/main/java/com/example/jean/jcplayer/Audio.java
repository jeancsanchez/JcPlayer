package com.example.jean.jcplayer;

import java.io.Serializable;

/**
 * Created by jean on 27/06/16.
 */

public class Audio implements Serializable {
    private int id;

    private String title;

    private int position;

    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}