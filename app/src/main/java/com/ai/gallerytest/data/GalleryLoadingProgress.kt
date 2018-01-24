package com.ai.gallerytest.data

import com.ai.gallerytest.presentation.model.GalleryImage


data class GalleryLoadingProgress(val loaded: Int, val size: Int, val image: GalleryImage)