# [ UNSTABLE ]

# JCPlayer
A simple JCAudio player for Android that you can plugin to your apps quickly get JCAudio playback working.


##Gradle Dependency
```Gradle
dependencies {
    // ... other dependencies
    compile 'io.github.jeancsanchez.jcplayer:jcplayer:0.0.2'
}
```


##Getting Started
You only need  a JCPlayerView on your Layout Activity. All the controls and everything else are created by the player view itself.
```xml
<com.example.jean.jcplayer.JCPlayerView
    android:id="@+id/jcplayer"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</com.example.jean.jcplayer.JCPlayerView>
```

##Code Setup
Create a list of Audio objects and set the values to it.
```Java
JCPlayerView player = (JCPlayerView) findViewById(R.id.jcplayer);
List<Audio> playList = new ArrayList<>();

Audio JCAudio1 = new Audio();
JCAudio1.setId(1);
JCAudio1.setPosition(1);
JCAudio1.setTitle("Track 1");
JCAudio1.setUrl("http://www.example.com.br/android/Music_01.mp3");

Audio JCAudio2 = new Audio();
JCAudio2.setId(2);
JCAudio2.setPosition(2);
JCAudio2.setTitle("Track 2");
JCAudio2.setUrl("http://www.example.com.br/android/Music_02.mp3");

playList.add(JCAudio1);
playList.add(JCAudio2);
```

###Init the playlist
```java
player.initPlaylist(playList, MainActivity.this);
```

###Call the notification player where you want.
```java
    player.createNotification(R.drawable.icon);
```
