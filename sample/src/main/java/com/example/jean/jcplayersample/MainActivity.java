package com.example.jean.jcplayersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    private JcPlayerView player;
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        player = (JcPlayerView) findViewById(R.id.jcplayer);

        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        jcAudios.add(JcAudio.createFromURL("http://www.villopim.com.br/android/Music_01.mp3"));
        //urls.add(new FileAndOrigin("http://www.villopim.com.br/android/Music_02.mp3", Origin.URL));
        //jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "13.mid"));
        //jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "123123.mid")); // invalid file path
        jcAudios.add(JcAudio.createFromAssets("49.v4.mid"));
        jcAudios.add(JcAudio.createFromAssets("tester", "aaa.mid")); // invalid assets file
        jcAudios.add(JcAudio.createFromRaw(R.raw.a_203));
        //jcAudios.add(JcAudio.createFromRaw("a_34", R.raw.a_34));
        //urls.add(new FileAndOrigin(String.valueOf(1), Origin.RAW)); // invalid raw file.
        player.initWithTitlePlaylist(jcAudios, "Awesome music");

        adapterSetup();
    }


    public void playAudio(JcAudio jcAudio){
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