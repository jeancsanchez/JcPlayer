package com.example.jean.jcplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioAssetsInvalidException;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioFilePathInvalidException;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioListNullPointerException;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioRawInvalidException;
import com.example.jean.jcplayer.JcPlayerExceptions.AudioUrlInvalidException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JcPlayerView extends LinearLayout implements
        JcPlayerService.JCPlayerServiceListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int PULSE_ANIMATION_DURATION = 200;
    private static final int TITLE_ANIMATION_DURATION = 600;

    private TextView txtCurrentMusic;
    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private List<JcAudio> playlist;
    private ProgressBar progressBarPlayer;
    private JcAudioPlayer jcAudioPlayer;
    private TextView txtDuration;
    private ImageButton btnNext;
    private SeekBar seekBar;
    private TextView txtCurrentDuration;
    private AssetFileDescriptor assetFileDescriptor;

    public JcPlayerView(Context context){
        super(context);
        init();
    }

    public JcPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public JcPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(){
        inflate(getContext(), R.layout.view_jcplayer, this);

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
     * @param playlist List of JcAudio objects that you want play
     */
    public void initPlaylist(List<JcAudio> playlist){
        if(this.playlist == null)
            this.playlist = new ArrayList<>();

        for(JcAudio audio : playlist){
            if( isAudioFileValid(audio.getPath(), audio.getOrigin()) )
             this.playlist.add(audio);

            else {
                throwError(audio.getPath(), audio.getOrigin());
            }
        }

        jcAudioPlayer = new JcAudioPlayer(getContext(), this.playlist, JcPlayerView.this);
    }


    /**
     * Initialize an anonymous playlist with a default title for all
     * @param jcAudios List of urls strings
     */
    public void initAnonPlaylist(List<JcAudio> jcAudios){
        if(playlist == null)
            playlist = new ArrayList<>();

        for(int i = 0; i < jcAudios.size(); i++){
            if(isAudioFileValid(jcAudios.get(i))){
                jcAudios.get(i).setId(i);
                jcAudios.get(i).setPosition(i);
                playlist.add(jcAudios.get(i));

                generateTitleAudio(getContext().getString(R.string.track_number, i+1), i);
            }else {
                throwError(jcAudios.get(i));
            }
        }

        jcAudioPlayer = new JcAudioPlayer(getContext(), playlist, JcPlayerView.this);
    }


    /**
     * Initialize an anonymous playlist, but with a custom title for all
     * @param jcAudios List of JcAudio files.
     * @param title Default title
     */
    public void initWithTitlePlaylist(List<JcAudio> jcAudios, String title){

        if(playlist == null)
            playlist = new ArrayList<>();

        for(int i = 0; i < jcAudios.size(); i++){
            // We don't catch for error here. Let user add a file eventhough not a valid file.
            jcAudios.get(i).setId(i);
            jcAudios.get(i).setPosition(i);
            playlist.add(jcAudios.get(i));

            generateTitleAudio(title + " " + String.valueOf(i+1), i);


            //}else {
                //try {
                //    throw new AudioUrlInvalidException(fileAndOrigins.get(i).getPath());
                //} catch (AudioUrlInvalidException e) {
                //    e.printStackTrace();
                //}
                //throwError(jcAudios.get(i));
            //}
        }

        jcAudioPlayer = new JcAudioPlayer(getContext(), playlist, JcPlayerView.this);
    }

    /**
     * Initialize an anonymous playlist, but with a custom title for all
     * @param title Audio title
     * @param path path of the file
     * @param origin origin of the file as in {@link Origin}
     */
    //TODO: Should we expose this to user?
    private void addAudio(String title, String path, Origin origin){
        if(isAudioFileValid(path, origin)) {
            if (playlist == null)
                playlist = new ArrayList<>();

            int lastPosition = playlist.size();
            playlist.add(lastPosition,
                    new JcAudio(path, title, /* id */ lastPosition + 1, /* position */ lastPosition + 1,
                        origin));

            if (jcAudioPlayer == null)
                jcAudioPlayer = new JcAudioPlayer(getContext(), playlist, JcPlayerView.this);
        }
        else {
            throwError(path, origin);
        }
    }

  /**
   * Adding new audio file to playlist
   * @param jcAudio Audio file generated from JcAudio factory
   */
  public void addAudio(JcAudio jcAudio){
        if(isAudioFileValid(jcAudio)) {
            if (playlist == null)
                playlist = new ArrayList<>();

            int lastPosition = playlist.size();
            jcAudio.setId(lastPosition + 1);
            jcAudio.setPosition(lastPosition + 1);
            playlist.add(lastPosition, jcAudio);

            if (jcAudioPlayer == null)
                jcAudioPlayer = new JcAudioPlayer(getContext(), playlist, JcPlayerView.this);
        }
        else {
            throwError(jcAudio);
        }
    }

    private void throwError(JcAudio jcAudio) {
        throwError(jcAudio.getPath(), jcAudio.getOrigin());
    }

    private void throwError(String path, Origin origin) {
        if(origin == Origin.URL) {
            throw new AudioUrlInvalidException(path);
        } else if(origin == Origin.RAW) {
            try {
                throw new AudioRawInvalidException(path);
            } catch (AudioRawInvalidException e) {
                e.printStackTrace();
            }
        } else if(origin == Origin.ASSETS) {
            try {
                throw new AudioAssetsInvalidException(path);
            } catch (AudioAssetsInvalidException e) {
                e.printStackTrace();
            }
        } else if(origin == Origin.FILE_PATH) {
            try {
                throw new AudioFilePathInvalidException(path);
            } catch (AudioFilePathInvalidException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateTitleAudio(String title, int position){
        playlist.get(position).setTitle(title);
    }

    private boolean isAudioFileValid(JcAudio jcAudio){
        return isAudioFileValid(jcAudio.getPath(), jcAudio.getOrigin());
    }

    private boolean isAudioFileValid(String path, Origin origin) {
        if(origin == Origin.URL) {
            return path.startsWith("http") || path.startsWith("https");
        } else if(origin == Origin.RAW) {
            assetFileDescriptor = null;
            assetFileDescriptor = getContext().getResources().openRawResourceFd(Integer.parseInt(path));
            return assetFileDescriptor != null;
        } else if(origin == Origin.ASSETS) {
            try {
                assetFileDescriptor = null;
                assetFileDescriptor = getContext().getAssets().openFd(path);
                return assetFileDescriptor != null;
            } catch (IOException e) {
                e.printStackTrace(); //TODO: need to give user more readable error.
                return false;
            }
        } else if(origin == Origin.FILE_PATH) {
            File file = new File(path);
            //TODO: find an alternative to checking if file is exist, this code is slower on average.
            //read more: http://stackoverflow.com/a/8868140
            return file.exists();
        } else {
            // We should never arrive here.
            return false; // We don't know what the origin of the Audio File
        }
    }

    @Override
    public void onClick(View view) {
        if(playlist != null)
            if(view.getId() ==  R.id.btn_play) {
                YoYo.with(Techniques.Pulse)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnPlay);

                if (btnPlay.getTag().equals(R.drawable.ic_pause_black))
                    pause();
                else
                    continueAudio();
            }

            if(view.getId() == R.id.btn_next) {
                YoYo.with(Techniques.Pulse)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnNext);
                next();
            }

            if(view.getId() == R.id.btn_prev) {
                YoYo.with(Techniques.Pulse)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnPrev);
                previous();
            }
    }

  //TODO: This is the old code. Still relevant?
    //public void playAudio(JcAudio JcAudio){
    //    showProgressBar();
    //    try {
    //        jcAudioPlayer.playAudio(JcAudio);
    //    }catch (AudioListNullPointerException e) {
    //        dismissProgressBar();
    //        e.printStackTrace();
    //    }
    //}

    public void playAudio(JcAudio jcAudio) {
        showProgressBar();

        if (playlist == null) {
            playlist = new ArrayList<>();
        }
        playlist.add(jcAudio);

        if(jcAudioPlayer == null) {
            jcAudioPlayer = new JcAudioPlayer(getContext(), playlist, JcPlayerView.this);
        }
        try {
            jcAudioPlayer.playAudio(jcAudio);
        } catch (AudioListNullPointerException e) {
            dismissProgressBar();
            e.printStackTrace();
        }
    }

  /**
   * Play audio from url
   * @param path path of file
   * @param title title of file
   * @param origin origin of the file
   */
    //TODO: Should we expose this to user?
    private void playAudio(String path, String title, Origin origin) {
        showProgressBar();

        JcAudio jcAudio = new JcAudio(path, title, origin);
        if (playlist == null)
            playlist = new ArrayList<>();
        playlist.add(jcAudio);

        if(jcAudioPlayer == null)
            jcAudioPlayer = new JcAudioPlayer(getContext(), playlist, JcPlayerView.this);

        try {
            jcAudioPlayer.playAudio(jcAudio);
        } catch (AudioListNullPointerException e) {
            dismissProgressBar();
            e.printStackTrace();
        }
    }

    private void next() {
        resetPlayerInfo();
        showProgressBar();

        try {
            jcAudioPlayer.nextAudio();
        } catch (AudioListNullPointerException e){
            dismissProgressBar();
            e.printStackTrace();
        }
    }

    private void continueAudio() {
        showProgressBar();

        try {
            jcAudioPlayer.continueAudio();
        } catch (AudioListNullPointerException e) {
            dismissProgressBar();
            e.printStackTrace();
        }
    }

    private void pause() {
        jcAudioPlayer.pauseAudio();
    }

    private void previous() {
        resetPlayerInfo();
        showProgressBar();

        try {
            jcAudioPlayer.previousAudio();
        } catch (AudioListNullPointerException e) {
            dismissProgressBar();
            e.printStackTrace();
        }
    }

    @Override
    public void onPreparedAudio(String audioName, int duration) {
        dismissProgressBar();
        resetPlayerInfo();

        long aux = duration / 1000;
        int minute = (int) (aux / 60);
        int second = (int) (aux % 60);

        final String sDuration =
                // Minutes
                (minute < 10 ? "0"+minute : minute+"")
                        + ":" +
                 // Seconds
                 (second < 10 ? "0"+second : second+"");

        seekBar.setMax(duration);

        txtDuration.post(new Runnable() {
            @Override
            public void run() {
                txtDuration.setText(sDuration);
            }
        });
    }

    @Override
    public void onCompletedAudio() {
        resetPlayerInfo();

        try {
            jcAudioPlayer.nextAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resetPlayerInfo(){
        seekBar.setProgress(0);
        txtCurrentMusic.setText("");
        txtCurrentDuration.setText(getContext().getString(R.string.play_initial_time));
        txtDuration.setText(getContext().getString(R.string.play_initial_time));
    }

    public void showProgressBar(){
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        btnPlay.setVisibility(Button.GONE);
        btnNext.setClickable(false);
        btnPrev.setClickable(false);
    }

    public void dismissProgressBar(){
        progressBarPlayer.setVisibility(ProgressBar.GONE);
        btnPlay.setVisibility(Button.VISIBLE);
        btnNext.setClickable(true);
        btnPrev.setClickable(true);
    }

    @Override
    public void onPaused() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_play_black, null));
        } else {
          btnPlay.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                                        R.drawable.ic_play_black, null));
        }
        btnPlay.setTag(R.drawable.ic_play_black);
    }

    @Override
    public void onContinueAudio() {
        dismissProgressBar();
    }

    @Override
    public void onPlaying() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_pause_black, null));
        } else {
          btnPlay.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                                        R.drawable.ic_pause_black, null));
        }
        btnPlay.setTag(R.drawable.ic_pause_black);
    }

    @Override
    public void onTimeChanged(long currentPosition) {
        long aux = currentPosition / 1000;
        int minutes = (int) (aux / 60);
        int seconds = (int) (aux % 60);
        final String sMinutes = minutes < 10 ? "0"+minutes : minutes+"";
        final String sSeconds = seconds < 10 ? "0"+seconds : seconds+"";

        seekBar.setProgress((int) currentPosition);
        txtCurrentDuration.post(new Runnable() {
            @Override
            public void run() {
                txtCurrentDuration.setText(String.valueOf(sMinutes + ":" + sSeconds));
            }
        });
    }

    @Override
    public void updateTitle(String title) {
        final String mTitle = title;

        YoYo.with(Techniques.FadeInLeft)
                .duration(TITLE_ANIMATION_DURATION)
                .playOn(txtCurrentMusic);

        txtCurrentMusic.post(new Runnable() {
            @Override
            public void run() {
                txtCurrentMusic.setText(mTitle);
            }
        });
    }

    /**
     * Create a notification player with same playlist with a custom icon.
     * @param iconResource icon path.
     */
    public void createNotification(int iconResource){
        if(jcAudioPlayer != null) jcAudioPlayer.createNewNotification(iconResource);
    }

    /**
     * Create a notification player with same playlist with a default icon
     */
    public void createNotification(){
        if(jcAudioPlayer != null) {
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For light theme
            jcAudioPlayer.createNewNotification(R.drawable.ic_notification_default_black);
          } else {
            // For dark theme
            jcAudioPlayer.createNewNotification(R.drawable.ic_notification_default_white);
          }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if(fromUser) jcAudioPlayer.seekTo(i);
    }

    public void kill() {
        if(jcAudioPlayer != null) jcAudioPlayer.kill();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        showProgressBar();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        dismissProgressBar();
    }

    public List<JcAudio> getMyPlaylist(){
        return playlist;
    }
}
