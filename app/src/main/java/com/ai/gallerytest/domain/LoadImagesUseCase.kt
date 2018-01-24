package com.ai.gallerytest.domain

import com.ai.gallerytest.data.IGalleryRepository
import com.ai.gallerytest.presentation.model.GalleryImage


class LoadImagesUseCase(val repository: IGalleryRepository) {
    fun execute(oldValues: ArrayList<GalleryImage>) = repository.getAllImages(oldValues)
}