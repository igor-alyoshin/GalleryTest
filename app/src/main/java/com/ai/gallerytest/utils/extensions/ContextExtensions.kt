package com.ai.gallerytest.utils.extensions

import android.content.Context
import android.os.Environment
import android.support.v4.os.EnvironmentCompat
import io.reactivex.Single
import java.io.File
import java.util.*


fun Context.getExternalStoragePaths(): Single<Collection<String>> {
    return Single.create { emitter ->
        emitter.onSuccess(externalCacheDirs?.filterNotNull()?.map { cacheFolder ->
            val path = cacheFolder.path
            val index = path.indexOf("/Android")
            if (path.length > 0 && index >= 0) {
                val resultStoragePath = path.dropLast(path.length - index)
                if (EnvironmentCompat.getStorageState(File(resultStoragePath)) == Environment.MEDIA_MOUNTED) {
                    resultStoragePath
                } else {
                    null
                }
            } else {
                null
            }
        }?.filterNotNull() ?: Collections.emptyList())
    }
}