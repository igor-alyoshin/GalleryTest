package com.ai.gallerytest.presentation.screens.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ai.gallerytest.data.GalleryImage
import com.ai.gallerytest.databinding.GalleryItemBinding
import com.ai.gallerytest.presentation.common.BaseSupplementingAdapter
import com.ai.gallerytest.presentation.common.BindableAdapter
import com.ai.gallerytest.presentation.common.formatFileSizeString


class GalleryAdapter : BaseSupplementingAdapter<GalleryImage, GalleryAdapter.ViewHolder>(), BindableAdapter<List<GalleryImage>> {

    override fun setData(data: List<GalleryImage>) {
        super.setData(data, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(GalleryItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(val binding: GalleryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: GalleryImage) {
            binding.image = image
            binding.formattedFileSize.text = formatFileSizeString(image.size)
            binding.executePendingBindings()
        }
    }
}