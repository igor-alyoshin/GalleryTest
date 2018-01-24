package com.ai.gallerytest.data

import com.ai.gallerytest.presentation.model.GalleryImage
import io.reactivex.Flowable
import io.reactivex.Observable


interface IGalleryRepository {
    fun getAllImages(oldValues: ArrayList<GalleryImage>): Observable<GalleryLoadingProgress>
    fun getImageEvents(): Flowable<Event>
}