package com.ai.gallerytest.data

import android.os.FileObserver
import android.os.FileObserver.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.ai.gallerytest.App
import com.ai.gallerytest.data.utils.RecursiveFileObserver
import com.ai.gallerytest.presentation.model.GalleryImage
import com.ai.gallerytest.utils.Utils
import com.ai.gallerytest.utils.extensions.getExternalStoragePaths
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


class GalleryRepository : IGalleryRepository {

    private val TAG = GalleryRepository::class.java.simpleName
    private val eventProcessor = PublishProcessor.create<Event>()
    private val disposable = CompositeDisposable()

    private var galleryFileObservers = ArrayList<FileObserver>()

    override fun getImageEvents() = eventProcessor.doOnLifecycle({
        synchronized(this) {
            if (!isFileWatching()) startFileWatching()
        }
    }, {
    }, {
        synchronized(this) {
            if (isFileWatching()) stopFileWatching()
        }
    })

    override fun getAllImages(oldValues: ArrayList<GalleryImage>): Observable<GalleryLoadingProgress> {
        return Observable.create<GalleryLoadingProgress> { emitter ->
            val cursor = App.instance.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.MediaColumns.DATA),
                    null,
                    null,
                    "${MediaStore.Images.Media.DATE_MODIFIED} DESC")
            var loaded = oldValues.size
            val size = cursor.count
            try {
                if (loaded < size) {
                    cursor.move(loaded)
                    val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    while (!emitter.isDisposed && cursor.moveToNext()) {
                        val path = cursor.getString(columnIndexData)
                        val file = File(path)
                        val galleryImage = GalleryImage(path, file.name, calculateMD5Hex(file), file.length())
                        emitter.onNext(GalleryLoadingProgress(++loaded, size, galleryImage))
                    }
                }
                emitter.onComplete()
            } finally {
                Utils.closeQuietly(cursor)
            }
        }
    }

    private fun isFileWatching() = disposable.size() > 0

    private fun startFileWatching() {
        disposable.add(App.instance.getExternalStoragePaths()
                .subscribe({ paths ->
                    paths.forEach { path ->
                        galleryFileObservers.add(object : RecursiveFileObserver(path, fileObserverFlags) {
                            override fun onEvent(eventId: Int, path: String) {
                                handleEvent(this, eventId, path)
                            }
                        }.also { observer ->
                            observer.startWatching()
                        })
                    }
                }))
    }

    private fun stopFileWatching() {
        disposable.clear()
        galleryFileObservers.forEach { observer ->
            observer.stopWatching()
        }
        galleryFileObservers.clear()
    }

    private fun handleEvent(observer: RecursiveFileObserver, eventId: Int, path: String) {
        val file = File(path)
        val extension = file.extension
        if (TextUtils.isEmpty(extension)) {
            when (eventId and FileObserver.ALL_EVENTS) {
                CREATE, MOVED_TO -> {
                    observer.startWatching(path)
                }
                DELETE, DELETE_SELF, MOVED_FROM -> {
                    observer.stopWatching(path)
                }
                else -> {
                    Log.w(TAG, "EVENT ${eventId} path=${path}")
                }
            }
        } else if (availableImageExtensions.contains(extension)) {
            val galleryImage = GalleryImage(path, file.name, calculateMD5Hex(file), file.length())
            eventProcessor.onNext(Event(eventId, galleryImage))
        }
    }

    private fun calculateMD5Hex(file: File): String? {
        try {
            if (!file.exists()) return null
            val digest = MessageDigest.getInstance(algoritm)
            digest.update(file.inputStream().readBytes())
            val md5sum = digest.digest()
            val bigInt = BigInteger(1, md5sum)
            val output = StringBuilder(bigInt.toString(16))
            while (output.length < 32) output.insert(0, '0')
            return String.format("%16s", output.toString()).replace(' ', '0')
        } catch (e: Exception) {
            Log.e(TAG, "Exception while getting md5: ", e)
            return null
        }
    }

    companion object {

        val algoritm = "MD5"
        val availableImageExtensions = arrayOf("gif", "png", "bmp", "jpg", "jpeg")
        val fileObserverFlags = CREATE or DELETE or DELETE_SELF or MOVED_FROM or MOVED_TO or MOVE_SELF

        @Volatile private var INSTANCE: GalleryRepository? = null

        fun getInstance(): GalleryRepository =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: GalleryRepository().also { INSTANCE = it }
                }
    }
}