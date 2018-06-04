package com.example.jean.jcplayersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.JcPlayerManager;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.general.PlayerUtil;
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.service.JcPlayerManagerListener;

import java.util.ArrayList;

public class CustomPlayerActivity extends AppCompatActivity
        implements OnInvalidPathListener, JcPlayerManagerListener {

    private static final String TAG = CustomPlayerActivity.class.getSimpleName();

    private JcPlayerManager jcPlayerManager;
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;
    private ImageButton btnPlay;
    private ImageButton btnNext;
    private ImageButton btnPrev;
    private ImageButton btnRepeat;
    private ImageButton btnRandom;
    private ProgressBar progress;
    private TextView txtDuration;
    private TextView txtCurDuration;
    private TextView txtCurAudio;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_layout);

        recyclerView = findViewById(R.id.customRecyclerView);
        btnPlay = findViewById(R.id.btnPlayCustom);
        btnNext = findViewById(R.id.btnNextCustom);
        btnPrev = findViewById(R.id.btnPrevCustom);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnRandom = findViewById(R.id.btnRandom);
        progress = findViewById(R.id.progressBarPlayerCustom);
        seekBar = findViewById(R.id.seekBarCustom);
        txtDuration = findViewById(R.id.txtDurationCustom);
        txtCurDuration = findViewById(R.id.txtCurrentDurationCustom);
        txtCurAudio = findViewById(R.id.txtCurrentMusicCustom);

        ArrayList<JcAudio> playlist = new ArrayList<>();
        playlist.add(JcAudio.createFromURL("url audio", "http://www.villopim.com.br/android/Music_01.mp3"));
        playlist.add(JcAudio.createFromRaw("Raw audio 2", R.raw.a_203));
        jcPlayerManager = JcPlayerManager.getInstance(this, playlist, this).get();
        adapterSetup();


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jcPlayerManager.playAudio(jcPlayerManager.getPlaylist().get(0));
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jcPlayerManager.nextAudio();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jcPlayerManager.previousAudio();
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                jcPlayerManager.setRepeat(true);
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                jcPlayerManager.setRandom(true);
            }
        });
    }

    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(jcPlayerManager.getPlaylist());
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                jcPlayerManager.playAudio(jcPlayerManager.getPlaylist().get(position));
            }

            @Override
            public void onSongItemDeleteClicked(int position) {
                Toast.makeText(CustomPlayerActivity.this, "Delete song at position " + position, Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        jcPlayerManager.createNewNotification(R.drawable.myicon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jcPlayerManager.kill();
    }

    @Override
    public void onPathError(JcAudio jcAudio) {

    }

    @Override
    public void onPreparedAudio(JcStatus status) {
        txtCurAudio.setText(status.getJcAudio().getTitle());
        txtDuration.setText(String.valueOf(status.getDuration()));
        txtCurDuration.setText(String.valueOf(status.getCurrentPosition()));
    }

    @Override
    public void onCompletedAudio() {
        jcPlayerManager.nextAudio();
    }

    @Override
    public void onPaused(JcStatus status) {

    }

    @Override
    public void onContinueAudio(JcStatus status) {
        jcPlayerManager.playAudio(status.getJcAudio());
    }

    @Override
    public void onPlaying(JcStatus status) {

    }

    @Override
    public void onTimeChanged(final JcStatus status) {
        seekBar.post(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress((int) status.getCurrentPosition());
                txtCurDuration.setText(PlayerUtil.toTimeSongString((int) status.getCurrentPosition()));
            }
        });
    }

    @Override
    public void onJcpError(Throwable throwable) {

    }
}