package com.ai.gallerytest.presentation

import android.app.Application
import com.ai.gallerytest.presentation.di.appModule
import com.ai.gallerytest.presentation.di.repositoryModule
import com.ai.gallerytest.presentation.di.viewModelModule
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(arrayListOf(appModule, repositoryModule, viewModelModule))
        }

        Picasso.setSingletonInstance(get())
    }

    companion object {
        lateinit var instance: App
    }
}