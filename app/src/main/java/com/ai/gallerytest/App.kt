package com.ai.gallerytest

import android.app.Application
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        val picasso = Picasso.Builder(this)
                .memoryCache(LruCache(50 * 1024 * 1024))
                .build()
        Picasso.setSingletonInstance(picasso)
    }

    companion object {
        lateinit var instance: App
    }
}