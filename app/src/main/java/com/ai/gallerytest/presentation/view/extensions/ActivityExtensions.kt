package com.ai.gallerytest.presentation.view.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

val REQUEST_CODE_PERMISSION_SETTINGS = 51

fun Activity.startApplicationDetailSettings() {
    startActivityForResult(Intent()
            .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", packageName, null)), REQUEST_CODE_PERMISSION_SETTINGS)
}