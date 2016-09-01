package com.example.jean.jcplayersample;

import android.app.Activity;
import android.os.Bundle;

import com.example.jean.jcplayer.JCAudio;
import com.example.jean.jcplayer.JCPlayerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private JCPlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = (JCPlayerView) findViewById(R.id.jcplayer);

        ArrayList<String> urls = new ArrayList<>();
        urls.add("http://www.villopim.com.br/android/Music_01.mp3");
        urls.add("http://www.villopim.com.br/android/Music_02.mp3");
        player.initWithTitlePlaylist(urls, "Awesome music");
    }

    @Override
    public void onPause(){
        super.onPause();
        player.createNotification();
    }
}
