package com.ai.gallerytest.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.ai.gallerytest.data.GalleryRepository
import com.ai.gallerytest.domain.LoadImagesUseCase
import com.ai.gallerytest.domain.UpdateImagesUseCase
import com.ai.gallerytest.presentation.viewmodel.GalleryViewModel


class ViewModelFactory(val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            val repository = GalleryRepository.getInstance()
            return GalleryViewModel(LoadImagesUseCase(repository), UpdateImagesUseCase(repository)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
