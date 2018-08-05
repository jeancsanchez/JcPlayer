package com.example.jean.jcplayer.model

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RawRes
import com.example.jean.jcplayer.general.Origin


/**
 * This class is an type of audio model .
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 27/06/16.
 * Jesus loves you.
 */
data class JcAudio constructor(
        var title: String,
        var position: Int? = -1,
        val path: String,
        val origin: Origin
) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            Origin.valueOf(source.readString())
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeValue(position)
        writeString(path)
        writeString(origin.name)
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<JcAudio> = object : Parcelable.Creator<JcAudio> {
            override fun createFromParcel(source: Parcel): JcAudio = JcAudio(source)
            override fun newArray(size: Int): Array<JcAudio?> = arrayOfNulls(size)
        }


        @JvmStatic
        fun createFromRaw(@RawRes rawId: Int): JcAudio {
            return JcAudio(title = rawId.toString(), path = rawId.toString(), origin = Origin.RAW)
        }

        @JvmStatic
        fun createFromRaw(title: String, @RawRes rawId: Int): JcAudio {
            return JcAudio(title = title, path = rawId.toString(), origin = Origin.RAW)
        }

        @JvmStatic
        fun createFromAssets(assetName: String): JcAudio {
            return JcAudio(title = assetName, path = assetName, origin = Origin.ASSETS)
        }

        @JvmStatic
        fun createFromAssets(title: String, assetName: String): JcAudio {
            return JcAudio(title = title, path = assetName, origin = Origin.ASSETS)
        }

        @JvmStatic
        fun createFromURL(url: String): JcAudio {
            return JcAudio(title = url, path = url, origin = Origin.URL)
        }

        @JvmStatic
        fun createFromURL(title: String, url: String): JcAudio {
            return JcAudio(title = title, path = url, origin = Origin.URL)
        }

        @JvmStatic
        fun createFromFilePath(filePath: String): JcAudio {
            return JcAudio(title = filePath, path = filePath, origin = Origin.FILE_PATH)
        }

        @JvmStatic
        fun createFromFilePath(title: String, filePath: String): JcAudio {
            return JcAudio(title = title, path = filePath, origin = Origin.FILE_PATH)
        }
    }
}