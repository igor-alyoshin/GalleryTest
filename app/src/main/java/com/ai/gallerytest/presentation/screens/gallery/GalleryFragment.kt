package com.ai.gallerytest.presentation.screens.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ai.gallerytest.databinding.FragmentGalleryBinding
import com.ai.gallerytest.presentation.common.BaseFragment
import com.ai.gallerytest.presentation.common.withNotNull
import org.koin.android.viewmodel.ext.android.viewModel

class GalleryFragment : BaseFragment<FragmentGalleryBinding>() {

    private val viewModel: GalleryViewModel by viewModel()
    private val adapter by lazy { GalleryAdapter() }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentGalleryBinding {
        return FragmentGalleryBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeGallery()
        withNotNull(binding) {
            setupSupportActionBar(toolbar)
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }
    }
}