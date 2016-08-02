package com.example.jean.jcplayer;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class JCPlayerView extends FrameLayout implements
        JCPlayerService.JCPlayerServiceListener,
        View.OnClickListener {

    private TextView txtCurrentMusic;
    private TextView txtDuration;
    private ImageButton btnNext;
    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private List<Audio> audioList;
    private ProgressBar progressBarPlayer;
    private RecyclerView recyclerView;
    private JCAudioPlayer jcAudioPlayer;
    private AudioAdapter audioAdapter;


    public JCPlayerView(Context context) {
        super(context);
        View rootView= (View) LayoutInflater.from(context).inflate(R.layout.activity_jcplayer, null);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.audioList);
        progressBarPlayer = (ProgressBar) rootView.findViewById(R.id.progress_bar_player);
        btnNext = (ImageButton) rootView.findViewById(R.id.btn_next);
        btnPrev = (ImageButton) rootView.findViewById(R.id.btn_prev);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btn_play);
        txtDuration = (TextView) rootView.findViewById(R.id.txt_duration);
        txtCurrentMusic = (TextView) rootView.findViewById(R.id.txt_current_music);
        btnPlay.setTag(R.drawable.play);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        /*test
        loadAudios();*/
    }


    public void initPlaylist(List<Audio> audioList){
        this.audioList = audioList;
        jcAudioPlayer = new JCAudioPlayer(getContext(), audioList, JCPlayerView.this);
        adapterSetup();
    }


//    private void loadAudios() {
//        audioList = new ArrayList<>();
//
//        Audio audio1 = new Audio();
//        audio1.setId(1);
//        audio1.setPosition(1);
//        audio1.setName("Track 1");
//        audio1.setUrl("http://www.villopim.com.br/android/Music_01.mp3");
//
//        Audio audio2 = new Audio();
//        audio2.setId(2);
//        audio2.setPosition(2);
//        audio2.setName("Track 2");
//        audio2.setUrl("http://www.villopim.com.br/android/Music_02.mp3");
//
//        audioList.add(audio1);
//        audioList.add(audio2);
//
////        jcAudioPlayer = new JCAudioPlayer(this, audioList, this);
//    }

    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);
        audioAdapter.setupItems(audioList);
    }

    @Override
    public void onClick(View view) {
        if(audioList != null)
            if(view.getId() ==  R.id.btn_play)
                if(btnPlay.getTag().equals(R.drawable.pause))
                    pause();
                else
                    continueAudio();

            if(view.getId() == R.id.btn_next)
                next();

            if(view.getId() == R.id.btn_prev)
                previous();
    }

    public void playAudio(Audio audio){
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        try {
            jcAudioPlayer.playAudio(audio);
        }catch (IllegalAccessException e) {
            progressBarPlayer.setVisibility(ProgressBar.GONE);
        }
    }

    private void next(){
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        try {
            jcAudioPlayer.nextAudio();
        }catch (IllegalAccessException e){
            progressBarPlayer.setVisibility(ProgressBar.GONE);
        }
    }

    private void continueAudio(){
        try {
            jcAudioPlayer.continueAudio();
        } catch (IllegalAccessException e) {
            progressBarPlayer.setVisibility(ProgressBar.GONE);
        }
    }

    private void pause() {
        jcAudioPlayer.pauseAudio();
    }

    private void previous(){
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        try {
            jcAudioPlayer.previousAudio();
        } catch (IllegalAccessException e) {
            progressBarPlayer.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public void onPreparedAudio() {
        progressBarPlayer.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onCompletedAudio() {
        try {
            jcAudioPlayer.nextAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaused() {
        btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.play, null));
        btnPlay.setTag(R.drawable.play);
    }

    @Override
    public void onPlaying() {
        btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null));
        btnPlay.setTag(R.drawable.pause);
    }

    @Override
    public void updateTime(String time) {
        final String mTime = time;

        new Runnable() {
                @Override
                public void run() {
                    txtDuration.setText(mTime);
                }
            };
    }

    @Override
    public void updateTitle(String title) {
        final String mTitle = title;

        new Runnable() {
            @Override
            public void run() {
                txtCurrentMusic.setText(mTitle);
            }
        };
    }

    public void createNotification(){
        if(jcAudioPlayer != null)
            jcAudioPlayer.createNewNotification();
    }
}
