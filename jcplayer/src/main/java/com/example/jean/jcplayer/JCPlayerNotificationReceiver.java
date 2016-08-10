package com.example.jean.jcplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jean.jcplayer.JCPlayerExceptions.AudioListNullPointer;

public class JCPlayerNotificationReceiver extends BroadcastReceiver {
    public JCPlayerNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        JCAudioPlayer jcAudioPlayer = JCAudioPlayer.getInstance();
        String action = "";

        if(intent.hasExtra(JCNotificationPlayer.ACTION))
            action = intent.getStringExtra(JCNotificationPlayer.ACTION);

        switch (action){
            case JCNotificationPlayer.PLAY:
                try {
                    jcAudioPlayer.continueAudio();
                    jcAudioPlayer.updateNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case JCNotificationPlayer.PAUSE:
                jcAudioPlayer.pauseAudio();
                jcAudioPlayer.updateNotification();
                break;

            case JCNotificationPlayer.NEXT:
                try {
                    jcAudioPlayer.nextAudio();
                } catch (AudioListNullPointer e) {
                    try {
                        jcAudioPlayer.continueAudio();
                    } catch (AudioListNullPointer e1) {
                        e1.printStackTrace();
                    }
                }
                break;

            case JCNotificationPlayer.PREVIOUS:
                try {
                    jcAudioPlayer.previousAudio();
                } catch (Exception e) {
                    try {
                        jcAudioPlayer.continueAudio();
                    } catch (AudioListNullPointer e1) {
                        e1.printStackTrace();
                    }
                }
                break;
        }
    }
}
