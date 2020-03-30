package com.ai.gallerytest.presentation.screens.start

import android.Manifest
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ai.gallerytest.R
import com.ai.gallerytest.databinding.FragmentStartBinding
import com.ai.gallerytest.presentation.common.BaseFragment
import com.ai.gallerytest.presentation.extensions.startApplicationDetailSettings
import com.ai.gallerytest.presentation.utils.DialogOnDeniedPermissionListener
import com.karumi.dexter.Dexter

class StartFragment : BaseFragment<FragmentStartBinding>() {

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentStartBinding {
        return FragmentStartBinding.inflate(inflater, container, false).also {
            it.view = this
        }
    }

    fun startGallery() {
        activity?.let {
            val listener = DialogOnDeniedPermissionListener.Builder
                    .withContext(it)
                    .withTitle(R.string.warning)
                    .withMessage(R.string.permission_not_enabled_description)
                    .withErrorListener { it.startApplicationDetailSettings() }
                    .withSuccessListener { findNavController().navigate(R.id.galleryFragment) }
                    .build()
            Dexter.withActivity(activity)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(listener)
                    .check()
        }
    }
}