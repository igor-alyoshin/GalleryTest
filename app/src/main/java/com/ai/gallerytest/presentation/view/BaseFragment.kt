package com.ai.gallerytest.presentation.view

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import com.ai.gallerytest.R
import com.ai.gallerytest.utils.Utils
import com.ai.gallerytest.presentation.view.extensions.startApplicationDetailSettings


abstract class BaseFragment : Fragment() {

    abstract fun onPermissionsGranted()
    abstract fun onPermissionsDenied()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (Utils.findDeniedPermissions(context).isEmpty()) {
                    onPermissionsGranted()
                } else {
                    onPermissionsDenied()
                    checkPermissions()
                }
            }
        }
    }

    protected fun checkPermissions(): Boolean {
        val deniedPermissions = Utils.findDeniedPermissions(context)
        if (deniedPermissions.isNotEmpty()) {
            if (Utils.shouldShowRequestPermissionRationale(activity, deniedPermissions)) {
                AlertDialog.Builder(context)
                        .setTitle(R.string.error)
                        .setMessage(R.string.permissions_error)
                        .setPositiveButton(R.string.action_ok, { _, _ -> activity.startApplicationDetailSettings() })
                        .create()
                        .show()
            } else {
                requestPermissions(Utils.PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
        return deniedPermissions.isEmpty()
    }

    companion object {
        val REQUEST_CODE_PERMISSIONS = 50
    }
}