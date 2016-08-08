# JCPlayer
A simple audio player for Android that you can plugin to your apps quickly get audio playback working.


#Gradle Dependency
```
dependencies {
    // ... other dependencies
    compile 'io.github.jeancsanchez.jcplayer:jcplayer:0.0.2'
}
```


#Getting Started
You only need  a JCPlayerView on your Layout Activity. All the controls and everything else are created by the player view itself.
```
<com.example.jean.jcplayer.JCPlayerView
    android:id="@+id/jcplayer"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</com.example.jean.jcplayer.JCPlayerView>
```

#Code Setup
Create a list of Audio objects and set the values to it.
```
player = (JCPlayerView) findViewById(R.id.jcplayer);
playList = new ArrayList<>();

Audio audio1 = new Audio();
audio1.setId(1);
audio1.setPosition(1);
audio1.setTitle("Track 1");
audio1.setUrl("http://www.example.com.br/android/Music_01.mp3");

Audio audio2 = new Audio();
audio2.setId(2);
audio2.setPosition(2);
audio2.setTitle("Track 2");
audio2.setUrl("http://www.example.com.br/android/Music_02.mp3");

playList.add(audio1);
playList.add(audio2);
```

Init the playlist
```
player.initPlaylist(playList, MainActivity.this);
```
