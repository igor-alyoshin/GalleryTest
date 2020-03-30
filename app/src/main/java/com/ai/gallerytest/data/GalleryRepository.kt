package com.ai.gallerytest.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.ai.gallerytest.data.extensions.closeQuietly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


class GalleryRepository(private val context: Context) : IGalleryRepository {

    @SuppressLint("Recycle")
    override fun loadImages(scope: CoroutineScope,
                            skip: Int): ReceiveChannel<GalleryLoadingProgress> {
        return scope.produce(Dispatchers.Default) {
            val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.MediaColumns.DATA),
                    null,
                    null,
                    "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            ) ?: return@produce
            var loaded = skip
            val size = cursor.count
            if (loaded < size) {
                cursor.move(loaded)
                val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                while (isActive && cursor.moveToNext()) {
                    val path = cursor.getString(columnIndexData)
                    val file = File(path)
                    val galleryImage =
                            GalleryImage(path, file.name, calculateMD5Hex(file), file.length())
                    send(GalleryLoadingProgress(++loaded, size, galleryImage))
                }
            }
            cursor.closeQuietly()
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
        private val TAG = GalleryRepository::class.java.simpleName

        const val algoritm = "MD5"
    }
}