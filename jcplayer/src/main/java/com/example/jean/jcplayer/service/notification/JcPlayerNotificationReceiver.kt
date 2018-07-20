package com.example.jean.jcplayer.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.jean.jcplayer.JcPlayerManager
import com.example.jean.jcplayer.general.errors.AudioListNullPointerException

class JcPlayerNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val jcPlayerManager = JcPlayerManager.getInstance(context)
        var action = ""

        if (intent.hasExtra(JcNotificationPlayer.ACTION)) {
            action = intent.getStringExtra(JcNotificationPlayer.ACTION)
        }

        when (action) {
            JcNotificationPlayer.PLAY -> try {
                jcPlayerManager.get()?.continueAudio()
                jcPlayerManager.get()?.updateNotification()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            JcNotificationPlayer.PAUSE -> try {
                jcPlayerManager.get()?.pauseAudio()
                jcPlayerManager.get()?.updateNotification()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            JcNotificationPlayer.NEXT -> try {
                jcPlayerManager.get()?.nextAudio()
            } catch (e: AudioListNullPointerException) {
                try {
                    jcPlayerManager.get()?.continueAudio()
                } catch (e1: AudioListNullPointerException) {
                    e1.printStackTrace()
                }

            }

            JcNotificationPlayer.PREVIOUS -> try {
                jcPlayerManager.get()?.previousAudio()
            } catch (e: Exception) {
                try {
                    jcPlayerManager.get()?.continueAudio()
                } catch (e1: AudioListNullPointerException) {
                    e1.printStackTrace()
                }
            }

        }
    }
}
