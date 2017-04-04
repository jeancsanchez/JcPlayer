package com.example.jean.jcplayer;

import android.content.Context;
import android.support.annotation.RawRes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.MockUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jeancarlos on 3/27/17.
 */

public class JcPlayerViewTest {

    @Mock
    Context context;

    private JcPlayerView jcPlayerView;
    private List<JcAudio> playlist;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        
        // TODO: Add a default constructor without Context dependencie for better test
        jcPlayerView = new JcPlayerView(context);


        playlist = new ArrayList<>();
        playlist.add(JcAudio.createFromAssets("fake asset"));
        playlist.add(JcAudio.createFromURL("fake url"));
        playlist.add(JcAudio.createFromFilePath("fake file path"));

        jcPlayerView.initPlaylist(playlist);

    }

    @Test
    public void player_has_same_size_of_playlist_user(){
        assertEquals(jcPlayerView.getMyPlaylist().size(), playlist.size());
    }
}
