[![](https://jitpack.io/v/jeancsanchez/JcPlayer.svg)](https://jitpack.io/#jeancsanchez/JcPlayer)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/74e4745bf1174c6e87c91e79418d61bc)](https://app.codacy.com/app/jeancsanchez/JcPlayer?utm_source=github.com&utm_medium=referral&utm_content=jeancsanchez/JcPlayer&utm_campaign=Badge_Grade_Dashboard)
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

## Note
 The layout of the playlist is responsability of the developer.

## TODO LIST ##
* [x] Set custom layouts for player.
* [ ] Add Instrumentation tests
* [ ] Add unity tests.

## Contributors
[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/0)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/0)[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/1)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/1)[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/2)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/2)[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/3)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/3)[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/4)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/4)[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/5)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/5)[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/6)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/6)[![](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/images/7)](https://sourcerer.io/fame/jeancsanchez/jeancsanchez/JcPlayer/links/7)
