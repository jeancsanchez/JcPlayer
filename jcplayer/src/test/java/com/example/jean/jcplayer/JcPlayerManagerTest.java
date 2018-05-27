package com.example.jean.jcplayer;

import android.content.Context;
import android.content.ServiceConnection;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.service.JcServiceConnection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 27/05/18.
 * Jesus loves you.
 */
public class JcPlayerManagerTest {

    @Mock
    private JcServiceConnection serviceConnection;

    @InjectMocks
    private JcPlayerManager jcPlayerManager;

    @Before
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void given_not_items_on_playlist__When_play_Then_no_play() {

    }
}
