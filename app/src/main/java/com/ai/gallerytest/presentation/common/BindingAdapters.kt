package com.ai.gallerytest.presentation.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ai.gallerytest.R
import com.ai.gallerytest.presentation.App
import com.squareup.picasso.Picasso
import java.io.File

interface BindableAdapter<T> {
    fun setData(data: T)
}

@BindingAdapter("data")
fun <T> setRecyclerViewData(recyclerView: RecyclerView, data: T) {
    if (recyclerView.adapter != null && recyclerView.adapter is BindableAdapter<*>) {
        (recyclerView.adapter as BindableAdapter<T>).setData(data)
    } else {
        throw NotImplementedError("adapter is null or not implements BindableAdapter")
    }
}

@BindingAdapter("galleryImage")
fun setGalleryImage(imageView: ImageView, filePath: String?) {
    if (!filePath.isNullOrBlank()) {
        Picasso.get()
                .load(File(filePath))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .resize(SCREEN_HEIGHT / 2, SCREEN_HEIGHT / 2)
                .centerCrop()
                .into(imageView)
    }
}

val SCREEN_HEIGHT by lazy { App.instance.resources.displayMetrics.heightPixels }