package com.example.jean.jcplayersample;

import android.app.Activity;
import android.os.Bundle;

import com.example.jean.jcplayer.JCAudio;
import com.example.jean.jcplayer.JCPlayerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private List<JCAudio> JCAudioList;
    private JCPlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = (JCPlayerView) findViewById(R.id.jcplayer);

        JCAudioList = new ArrayList<>();
        JCAudio JCAudio1 = new JCAudio();
        JCAudio1.setId(1);
        JCAudio1.setPosition(1);
        JCAudio1.setTitle("Ari - Alteza");
        JCAudio1.setUrl("http://10.0.1.61:8080/Ari%20-%20Alteza%20(Part.%20Banda%20Cone).mp3");

        JCAudio JCAudio2 = new JCAudio();
        JCAudio2.setId(2);
        JCAudio2.setPosition(2);
        JCAudio2.setTitle("Cacife Clandestino - Eu e você contra o mundo");
        JCAudio2.setUrl("http://10.0.1.61:8080/Cacife%20Clandestino%20-%20Eu%20e%20Voc%C3%AA%20Contra%20o%20Mundo%20part.%20Reis%20Do%20Nada.mp3");


        JCAudioList.add(JCAudio1);
        JCAudioList.add(JCAudio2);

        player.initPlaylist(JCAudioList, this);

    }

    @Override
    public void onPause(){
        super.onPause();
        player.createNotification(R.drawable.myicon);
    }
}
