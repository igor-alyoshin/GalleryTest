package com.ai.gallerytest.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ai.gallerytest.R
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : BaseFragment() {

    private var mListener: IStartFragmentInteraction? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IStartFragmentInteraction) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement IStartFragmentInteraction")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startGalleryButton.setOnClickListener {
            startGalleryButton.isEnabled = false
            mListener?.onStartGalleryButtonClick()
        }
    }

    override fun onStart() {
        super.onStart()
        startGalleryButton.isEnabled = checkPermissions()
    }

    override fun onPermissionsGranted() {
        startGalleryButton.isEnabled = true
    }

    override fun onPermissionsDenied() {
        startGalleryButton.isEnabled = false
    }

    interface IStartFragmentInteraction {
        fun onStartGalleryButtonClick()
    }

    companion object {
        fun newInstance() = StartFragment()
    }
}