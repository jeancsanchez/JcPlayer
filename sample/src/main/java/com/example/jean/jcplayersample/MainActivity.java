package com.example.jean.jcplayersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jean.jcplayer.JCAudio;
import com.example.jean.jcplayer.JCPlayerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    private JCPlayerView player;
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        player = (JCPlayerView) findViewById(R.id.jcplayer);

        ArrayList<String> urls = new ArrayList<>();
        urls.add("http://www.villopim.com.br/android/Music_01.mp3");
        urls.add("http://www.villopim.com.br/android/Music_02.mp3");
        player.initWithTitlePlaylist(urls, "Awesome music");

        adapterSetup();
    }


    public void playAudio(JCAudio jcAudio){
        player.playAudio(jcAudio);
    }

    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);
        audioAdapter.setupItems(player.getMyPlaylist());
    }


    @Override
    public void onPause(){
        super.onPause();
        player.createNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.kill();
    }
}