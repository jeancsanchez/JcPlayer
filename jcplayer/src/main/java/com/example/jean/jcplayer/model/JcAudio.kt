package com.example.jean.jcplayer.model

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RawRes
import com.example.jean.jcplayer.general.Origin
import kotlinx.android.parcel.Parcelize


/**
 * This class is an type of audio model .
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 27/06/16.
 * Jesus loves you.
 */
@Parcelize
data class JcAudio constructor(
        var title: String,
        var position: Int? = -1,
        val path: String,
        val origin: Origin
) : Parcelable {

    companion object {

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