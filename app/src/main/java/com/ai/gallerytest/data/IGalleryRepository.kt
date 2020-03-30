package com.ai.gallerytest.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel


interface IGalleryRepository {
    fun loadImages(scope: CoroutineScope, skip: Int): ReceiveChannel<GalleryLoadingProgress>
}