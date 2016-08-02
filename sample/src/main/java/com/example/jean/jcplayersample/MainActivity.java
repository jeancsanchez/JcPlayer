package com.example.jean.jcplayersample;

import android.app.Activity;
import android.os.Bundle;

import com.example.jean.jcplayer.Audio;
import com.example.jean.jcplayer.JCPlayerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private List<Audio> audioList;
    private JCPlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = (JCPlayerView) findViewById(R.id.jcplayer);

        audioList = new ArrayList<>();
        Audio audio1 = new Audio();
        audio1.setId(1);
        audio1.setPosition(1);
        audio1.setTitle("Track 1");
        audio1.setUrl("http://www.villopim.com.br/android/Music_01.mp3");

        Audio audio2 = new Audio();
        audio2.setId(2);
        audio2.setPosition(2);
        audio2.setTitle("Track 2");
        audio2.setUrl("http://www.villopim.com.br/android/Music_02.mp3");


        player.initPlaylist(audioList);
    }
}
