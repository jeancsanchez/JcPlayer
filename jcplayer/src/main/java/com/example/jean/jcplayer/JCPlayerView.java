package com.example.jean.jcplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jean.jcplayer.JCPlayerExceptions.AudioListNullPointer;

import java.util.List;

public class JCPlayerView extends LinearLayout implements
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


    public JCPlayerView(Context context){
        super(context);
        init();
    }

    public JCPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JCPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(){
        inflate(getContext(), R.layout.view_jcplayer, this);

        this.recyclerView = (RecyclerView) findViewById(R.id.audioList);
        this.progressBarPlayer = (ProgressBar) findViewById(R.id.progress_bar_player);
        this.btnNext = (ImageButton) findViewById(R.id.btn_next);
        this.btnPrev = (ImageButton) findViewById(R.id.btn_prev);
        this.btnPlay = (ImageButton) findViewById(R.id.btn_play);
        this.txtDuration = (TextView) findViewById(R.id.txt_duration);
        this.txtCurrentMusic = (TextView) findViewById(R.id.txt_current_music);
        this.btnPlay.setTag(R.drawable.play);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
    }


    public void initPlaylist(List<Audio> audioList, Context context){
        this.audioList = audioList;
        jcAudioPlayer = new JCAudioPlayer(context, audioList, JCPlayerView.this);
        adapterSetup();
    }


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
        }catch (AudioListNullPointer e) {
            progressBarPlayer.setVisibility(ProgressBar.GONE);
            e.printStackTrace();
        }
    }

    private void next(){
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        try {
            jcAudioPlayer.nextAudio();
        }catch (AudioListNullPointer e){
            progressBarPlayer.setVisibility(ProgressBar.GONE);
            e.printStackTrace();
        }
    }

    private void continueAudio(){
        try {
            jcAudioPlayer.continueAudio();
        } catch (AudioListNullPointer e) {
            progressBarPlayer.setVisibility(ProgressBar.GONE);
            e.printStackTrace();
        }
    }

    private void pause() {
        jcAudioPlayer.pauseAudio();
    }

    private void previous(){
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        try {
            jcAudioPlayer.previousAudio();
        } catch (AudioListNullPointer e) {
            progressBarPlayer.setVisibility(ProgressBar.GONE);
            e.printStackTrace();
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

        txtDuration.post(new Runnable() {
            @Override
            public void run() {
                txtDuration.setText(mTime);
            }
        });
    }

    @Override
    public void updateTitle(String title) {
        final String mTitle = title;
        txtCurrentMusic.post(new Runnable() {
            @Override
            public void run() {
                txtCurrentMusic.setText(mTitle);
            }
        });
    }

    public void createNotification(int iconResource){
        if(jcAudioPlayer != null)
            jcAudioPlayer.createNewNotification(iconResource);
    }
}
