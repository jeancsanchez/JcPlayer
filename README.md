# JcPlayer
[ ![Download](https://api.bintray.com/packages/jeancsanchez/maven/JcPlayer/images/download.svg) ](https://bintray.com/jeancsanchez/maven/JcPlayer/_latestVersion)
</br>
A simple audio player for Android that you can plugin to your apps quickly get audio playback working.
</br></br>
![alt tag]
(https://github.com/jeancsanchez/JcPlayer/blob/master/sample/jcplayer-gif-definitive.gif)

## New features
- Raw files
- Asset Files
- Local files

## Tested files
- http://xxxx/abc.mp3

## Not tested URLs
- http://xxxx/abc.m4a
- http://xxxx:1232
- http://xxxx/abc.pls
- http://xxxx/abc.ram
- http://xxxx/abc.wax
- rtmp://xxxx
- http://xxxx/abc.aspx
- http://xxxx/abc.php
- http://xxxx/abc.html
- mms://xxxx

##Maven
```Gradle
allprojects {
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/jeancsanchez/maven"
        }
    }
}
```
##Gradle Dependency
```Gradle
dependencies {
    // ... other dependencies
    compile 'io.github.jeancsanchez.jcplayer:jcplayer:2.6.0-alpha'
}
```


##Getting Started
You only need  a JcPlayerView on your Layout Activity/Fragment. All the controls and everything else are created by the player view itself.
```xml
<com.example.jean.jcplayer.JcPlayerView
    android:id="@+id/jcplayer"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</com.example.jean.jcplayer.JcPlayerView>
```

##Code Setup
####Find your JcPlayerView xml and...
```java
    jcplayerView = (JcPlayerView) findViewById(R.id.jcplayerView);
```

###Option 1: Just init a playlist
```java
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    jcAudios.add(JcAudio.createFromURL("url audio","http://xxx/audio.mp3"));
    jcAudios.add(JcAudio.createFromAssets("Asset audio", "audio.mp3"));
    jcAudios.add(JcAudio.createFromRaw("Raw audio", R.raw.audio));

    jcplayerView.initPlaylist(jcAudios);
```

###Option 2: Initialize an anonymous playlist with a default title for all
```java
    jcplayerView.addAudio(JcAudio.createFromURL("url audio","http://xxx/audio.mp3"));
    jcplayerView.addAudio(JcAudio.createFromAssets("audio.mp3"));
    jcplayerView.addAudio(JcAudio.createFromRaw(R.raw.audio));

    jcplayerView.initAnonPlaylist(jcAudios);
```

###Option 3: Initialize an playlist with a custom title for all
```java    
    jcplayerView.initWithTitlePlaylist(urls, "Awesome music");
```

###Call the notification player where you want.
```java
    jcplayerView.createNotification(); // default icon
```
OR
```java
    jcplayerView.createNotification(R.drawable.myIcon); // Your icon resource
```

###How can I get callbacks of player status?
```java
    MyActivity implements JcPlayerService.JcPlayerServiceListener {
        ....
        jcplayerView.registerServiceListener(this);
        // Just be happy :D
 }
```
###Note
> - The list view is developer responsibilty

## TODO LIST##

* [ ] Support others audio formats.
* [ ] Support and test others urls.
