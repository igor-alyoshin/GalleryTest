package com.ai.gallerytest.presentation.di

import com.ai.gallerytest.data.GalleryRepository
import com.ai.gallerytest.data.IGalleryRepository
import com.ai.gallerytest.presentation.screens.gallery.GalleryViewModel
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single<Picasso> {
        Picasso.Builder(androidContext())
                .memoryCache(LruCache(100 * 1024 * 1024))
                .build()
    }
}

val repositoryModule = module {
    single<IGalleryRepository> {
        GalleryRepository(androidContext())
    }
}

val viewModelModule = module {
    viewModel {
        GalleryViewModel(get())
    }
}