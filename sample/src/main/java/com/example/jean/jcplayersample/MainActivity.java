package com.example.jean.jcplayersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerService;
import com.example.jean.jcplayer.JcPlayerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JcPlayerService.OnInvalidPathListener {
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
        jcAudios.add(JcAudio.createFromURL("url audio","http://www.villopim.com.br/android/Music_01.mp3"));
        jcAudios.add(JcAudio.createFromAssets("Asset audio", "49.v4.mid"));
        jcAudios.add(JcAudio.createFromRaw("Raw audio", R.raw.a_34));
        jcAudios.add(JcAudio.createFromFilePath("File directory audio", this.getFilesDir() + "/" + "CANTO DA GRAÚNA.mp3"));
        jcAudios.add(JcAudio.createFromAssets("I am invalid audio", "aaa.mid")); // invalid assets file
        player.initPlaylist(jcAudios);


//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "13.mid"));
//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "123123.mid")); // invalid file path
//        jcAudios.add(JcAudio.createFromAssets("49.v4.mid"));
//        jcAudios.add(JcAudio.createFromRaw(R.raw.a_203));
//        jcAudios.add(JcAudio.createFromRaw("a_34", R.raw.a_34));
//        player.initWithTitlePlaylist(jcAudios, "Awesome music");


//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "13.mid"));
//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "123123.mid")); // invalid file path
//        jcAudios.add(JcAudio.createFromAssets("49.v4.mid"));
//        jcAudios.add(JcAudio.createFromRaw(R.raw.a_203));
//        jcAudios.add(JcAudio.createFromRaw("a_34", R.raw.a_34));
//        player.initAnonPlaylist(jcAudios);

//        Adding new audios to playlist
//        player.addAudio(JcAudio.createFromURL("url audio","http://www.villopim.com.br/android/Music_01.mp3"));
//        player.addAudio(JcAudio.createFromAssets("49.v4.mid"));
//        player.addAudio(JcAudio.createFromRaw(R.raw.a_34));
//        player.addAudio(JcAudio.createFromFilePath(this.getFilesDir() + "/" + "121212.mmid"));

        player.registerInvalidPathListener(this);
        adapterSetup();
    }


    public void playAudio(JcAudio jcAudio){
        player.playAudio(jcAudio);
        Toast.makeText(this, player.getCurrentAudio().getOrigin().toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPathError(JcAudio jcAudio) {
        Toast.makeText(this, jcAudio.getPath() + " with problems", Toast.LENGTH_LONG).show();
//        player.removeAudio(jcAudio);
//        player.next();
    }
}