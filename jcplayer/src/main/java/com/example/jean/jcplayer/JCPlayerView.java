package com.example.jean.jcplayer;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jean.jcplayer.JCPlayerExceptions.AudioListNullPointer;

import java.util.List;

public class JCPlayerView extends LinearLayout implements
        JCPlayerService.JCPlayerServiceListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView txtCurrentMusic;
    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private List<Audio> playlist;
    private ProgressBar progressBarPlayer;
    private RecyclerView recyclerView;
    private JCAudioPlayer jcAudioPlayer;
    private AudioAdapter audioAdapter;
    private TextView txtDuration;
    private ImageButton btnNext;
    private SeekBar seekBar;
    private TextView txtCurrentDuration;


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
        this.txtDuration = (TextView) findViewById(R.id.txt_total_duration);
        this.txtCurrentDuration = (TextView) findViewById(R.id.txt_current_duration);
        this.txtCurrentMusic = (TextView) findViewById(R.id.txt_current_music);
        this.seekBar = (SeekBar) findViewById(R.id.seek_bar);
        this.btnPlay.setTag(R.drawable.ic_play_black);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }


    /**
     * Initialize the playlist and controls.
     * @param playlist List of the Audio objects that you want play
     * @param context Context of the your application
     */
    public void initPlaylist(List<Audio> playlist, Context context){
        this.playlist = playlist;
        jcAudioPlayer = new JCAudioPlayer(context, playlist, JCPlayerView.this);
        adapterSetup();
    }


    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);
        audioAdapter.setupItems(playlist);
    }

    @Override
    public void onClick(View view) {
        if(playlist != null)
            if(view.getId() ==  R.id.btn_play)
                if(btnPlay.getTag().equals(R.drawable.ic_pause_black))
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
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
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
    public void onPreparedAudio(String audioName, int duration) {
        final int mDuration = duration;

        progressBarPlayer.setVisibility(ProgressBar.GONE);
        seekBar.setMax(duration);

        txtDuration.post(new Runnable() {
            @Override
            public void run() {
                txtDuration.setText(String.valueOf(mDuration));
            }
        });
    }

    @Override
    public void onCompletedAudio() {
        seekBar.setProgress(0);
        seekBar.setMax(0);

        try {
            jcAudioPlayer.nextAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaused() {
        btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_black, null));
        btnPlay.setTag(R.drawable.ic_play_black);
    }

    @Override
    public void onContinueAudio() {
        progressBarPlayer.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onPlaying() {
        btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_black, null));
        btnPlay.setTag(R.drawable.ic_pause_black);
    }

    @Override
    public void onTimeChanged(int minutes, int seconds) {
        final String sMinutes = minutes < 10 ? "0"+minutes : minutes+"";
        final String sSeconds = seconds < 10 ? "0"+seconds : seconds+"";
        seekBar.setProgress(minutes + seconds);

        txtCurrentDuration.post(new Runnable() {
            @Override
            public void run() {
                txtCurrentDuration.setText(String.valueOf(sMinutes + ":" + sSeconds));
            }
        });
    }

    @Override
    public void updateTime(String time) {

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


    /**
     * Create a notification player with same playlist.
     * @param iconResource Path of the icon.
     */
    public void createNotification(int iconResource){
        if(jcAudioPlayer != null)
            jcAudioPlayer.createNewNotification(iconResource);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if(fromUser)
            jcAudioPlayer.seekTo(i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
