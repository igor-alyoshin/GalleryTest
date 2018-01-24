package com.ai.gallerytest.domain

import com.ai.gallerytest.data.IGalleryRepository


class UpdateImagesUseCase(val repository: IGalleryRepository) {
    fun execute() = repository.getImageEvents()
}