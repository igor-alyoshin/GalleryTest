package com.ai.gallerytest.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import java.io.Closeable
import java.text.DecimalFormat
import java.util.*

object Utils {
    fun findDeniedPermissions(context: Context): List<String> {
        val denyPermissions = ArrayList<String>()
        if (!isPostMarshmallow()) return denyPermissions
        for (value in PERMISSIONS) {
            if (PermissionChecker.checkSelfPermission(context, value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value)
            }
        }
        return denyPermissions
    }

    fun shouldShowRequestPermissionRationale(activity: Activity, permission: List<String>): Boolean {
        var result = false
        for (value in permission) {
            result = result or ActivityCompat.shouldShowRequestPermissionRationale(activity, value)
        }
        return result
    }

    fun isPostMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun closeQuietly(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (rethrown: RuntimeException) {
                throw rethrown
            } catch (ignored: Exception) {
            }
        }
    }

    fun formatFileSizeString(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    val PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}