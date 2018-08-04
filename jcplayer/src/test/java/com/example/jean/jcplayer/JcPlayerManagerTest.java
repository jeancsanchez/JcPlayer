//package com.example.jean.jcplayer;
//
//import com.example.jean.jcplayer.general.Origin;
//import com.example.jean.jcplayer.general.errors.EmptyPlaylistException;
//import com.example.jean.jcplayer.model.JcAudio;
//import com.example.jean.jcplayer.JcPlayerManagerListener;
//import com.example.jean.jcplayer.service.JcServiceConnection;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//import org.mockito.Mock;
//
//import java.util.ArrayList;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.MockitoAnnotations.initMocks;
//
///**
// * @author Jean Carlos (Github: @jeancsanchez)
// * @date 27/05/18.
// * Jesus loves you.
// */
//
//@RunWith(JUnit4.class)
//public class JcPlayerManagerTest {
//
//    @Mock
//    private JcServiceConnection jcServiceConnection;
//
//    @Mock
//    private JcPlayerManagerListener managerListener;
//
//    private JcPlayerManager jcPlayerManager;
//
//    private JcAudio jcAudio;
//
//    private ArrayList<JcAudio> playlist;
//
//    @Before
//    public void setUp() {
//        initMocks(this);
//
//        jcPlayerManager = JcPlayerManager.getInstance(jcServiceConnection, null, null);
//        jcPlayerManager.setJcPlayerManagerListener(managerListener);
//
//        jcAudio = new JcAudio("Fake audio", "FakePath", Origin.URL);
//        playlist = new ArrayList<>();
//        playlist.add(jcAudio);
//
//    }
//
//
//    @Test
//    public void given_no_items_on_playlist__When_play__Then_notify_error() {
//        playlist.clear();
//        jcPlayerManager.setPlaylist(playlist);
//
//        jcPlayerManager.playAudio(jcAudio);
//
//        verify(managerListener).onJcpError(any(EmptyPlaylistException.class));
//        verifyNoMoreInteractions(managerListener);
//    }
//}
