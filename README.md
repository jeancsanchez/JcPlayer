# [ UNSTABLE ]

# JCPlayer
A simple JCAudio player for Android that you can plugin to your apps quickly get JCAudio playback working.


##Gradle Dependency
```Gradle
dependencies {
    // ... other dependencies
    compile 'io.github.jeancsanchez.jcplayer:jcplayer:1.0.0-alpha'
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
###Option 1: Add an url and a title for each audio.
```Java
    player.addAudio("My title", "http://www.example.com.br/android/Music_01.mp3");
```
###Option 2: Initialize an anonymous playlist with a default title for all
```java
    ArrayList<String> urls = new ArrayList<>();
    urls.add("http://www.example.com.br/android/Music_01.mp3");
    urls.add("http://www.example.com.br/android/Music_02.mp3");
    
    player.initAnonPlaylist(urls);
```

###Option 3: Initialize an anonymous playlist, but with a custom title for all
```java
    ArrayList<String> urls = new ArrayList<>();
    urls.add("http://www.example.com.br/android/Music_01.mp3");
    urls.add("http://www.example.com.br/android/Music_02.mp3");
    
    player.initWithTitlePlaylist(urls, "Awesome music");
```

###Option 4: Initialize a list of JCAudio objects
```java
    List<Audio> playList = new ArrayList<>();
    Audio JCAudio1 = new Audio();
    JCAudio1.setId(1);
    JCAudio1.setPosition(1);
    JCAudio1.setTitle("Track 1");
    JCAudio1.setUrl("http://www.example.com.br/android/Music_01.mp3");
    playList.add(JCAudio2);
    
    player.initPlaylist(playList);
```

###Call the notification player where you want.
```java
    player.createNotification();
```
