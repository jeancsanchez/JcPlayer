[![](https://jitpack.io/v/jeancsanchez/JcPlayer.svg)](https://jitpack.io/#jeancsanchez/JcPlayer)
</br></br>

<h1 align=center>
<img src="logo.png" width=60%>
</h1>

A simple audio player for Android that you can plugin to your apps quickly get audio playback working.
</br></br>

![](https://github.com/jeancsanchez/JcPlayer/blob/master_v2/sample/jcplayer_2.gif)

## New features
- Raw files
- Asset Files
- Custom layout

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

## Support us

You can support us by becoming a patron on Patreon, any support is much appreciated.

[![Patreon](https://c5.patreon.com/external/logo/become_a_patron_button.png)](https://www.patreon.com/jeancsanchez)


## Gradle Dependency (Project level)
```Gradle
allprojects {
	repositories {
		jcenter()
	        maven { url "https://jitpack.io" }
	    }
	}
```
## Gradle Dependency (Module level) 
```Gradle
dependencies {
    // ... other dependencies
     implementation 'com.github.jeancsanchez:JcPlayer:{version}'
}
```


## Getting Started
You only need  a JcPlayerView on your Layout Activity/Fragment. All the controls and everything else are created by the player view itself.
```xml
<com.example.jean.jcplayer.view.JcPlayerView
    android:id="@+id/jcplayer"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

## Code Setup
#### Find your JcPlayerView xml and...
```java
    jcplayerView = (JcPlayerView) findViewById(R.id.jcplayerView);
```

### Option 1: Just init a playlist
```java
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    jcAudios.add(JcAudio.createFromURL("url audio","http://xxx/audio.mp3"));
    jcAudios.add(JcAudio.createFromAssets("Asset audio", "audio.mp3"));
    jcAudios.add(JcAudio.createFromRaw("Raw audio", R.raw.audio));

    jcplayerView.initPlaylist(jcAudios, null);
```

### Option 2: Initialize an anonymous playlist with a default title for all
```java
    jcplayerView.initAnonPlaylist(jcAudios);
```

### Option 3: Initialize an playlist with a custom title for all
```java    
    jcplayerView.initWithTitlePlaylist(urls, "Awesome music");
```

### Call the notification player where you want.
```java
    jcplayerView.createNotification(); // default icon
```
OR
```java
    jcplayerView.createNotification(R.drawable.myIcon); // Your icon resource
```

### How can I get callbacks of player status?
```java
    MyActivity implements JcPlayerManagerListener {
        ....
        jcplayerView.setJcPlayerManagerListener(this);
        // Just be happy :D
 }
```

## Custom layout
You can customize the player layout by manipulating these attributes.
```xml
        app:next_icon
	app:next_icon_color
	app:pause_icon
	app:pause_icon_color
	app:play_icon
	app:play_icon_color
	app:previous_icon
	app:previous_icon_color
	app:progress_color
	app:random_icon_color
	app:repeat_icon
	app:repeat_icon_color
	app:seek_bar_color
	app:text_audio_current_duration_color
	app:text_audio_duration_color
	app:text_audio_title_color
```
### Please, if you liked this project or help you to do your job, support me by being a sponsor <3

## TODO LIST ##
* [x] Set custom layouts for player.
* [ ] Add Instrumentation tests
* [ ] Add unit tests.

