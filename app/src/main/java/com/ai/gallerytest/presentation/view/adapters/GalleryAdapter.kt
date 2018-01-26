package com.ai.gallerytest.presentation.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.ai.gallerytest.R
import com.ai.gallerytest.presentation.model.GalleryImage
import com.ai.gallerytest.utils.Utils
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class GalleryAdapter(val context: Context) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var mData = ArrayList<GalleryImage>()

    fun setData(list: List<GalleryImage>?) {
        ArrayList(list ?: Collections.emptyList()).let { data ->
            applyAndAnimateRemovals(data)
            applyAndAnimateAdditions(data)
            applyAndAnimateMovedItems(data)
        }
    }

    private fun applyAndAnimateRemovals(newImages: ArrayList<GalleryImage>) {
        for (i in mData.indices.reversed()) {
            if (!newImages.contains(mData[i])) removeItem(i)
        }
    }

    private fun applyAndAnimateAdditions(newImages: ArrayList<GalleryImage>) {
        for (i in newImages.indices) {
            val image = newImages[i]
            if (!mData.contains(image)) addItem(i, image)
        }
    }

    private fun applyAndAnimateMovedItems(newImages: ArrayList<GalleryImage>) {
        for (toPosition in newImages.indices.reversed()) {
            val image = newImages[toPosition]
            val fromPosition = mData.indexOf(image)
            if (fromPosition >= 0 && fromPosition != toPosition) moveItem(fromPosition, toPosition)
        }
    }

    fun addNewestItem(image: GalleryImage) {
        mData.add(0, image)
        notifyItemInserted(0)
    }

    fun addOldestItems(images: Collection<GalleryImage>, preloading: Boolean) {
        if (preloading) {
            for (image in images) {
                if (!mData.contains(image)) addItem(mData.size, image)
            }
        } else {
            mData.addAll(images)
            notifyItemRangeInserted(mData.size - images.size - 1, images.size)
        }
    }

    fun removeItem(image: GalleryImage) {
        val fromPosition = mData.indexOf(image)
        if (fromPosition < 0) return
        mData.removeAt(fromPosition)
        notifyItemRemoved(fromPosition)
    }

    fun replaceItem(source: GalleryImage, dest: GalleryImage) {
        val position = mData.indexOf(source)
        if (position < 0) return
        mData.set(position, dest)
        notifyItemChanged(position)
    }

    private fun addItem(toPosition: Int, image: GalleryImage) {
        val toPositionLocal = Math.min(toPosition, mData.size)
        mData.add(toPositionLocal, image)
        notifyItemInserted(toPositionLocal)
    }

    private fun moveItem(fromPosition: Int, toPosition: Int) {
        val image = mData.removeAt(fromPosition)
        val toPositionLocal = Math.min(toPosition, mData.size)
        mData.add(toPositionLocal, image)
        notifyItemMoved(fromPosition, toPositionLocal)
    }

    private fun removeItem(fromPosition: Int): GalleryImage? {
        if (fromPosition < 0) return null
        val image = mData.removeAt(fromPosition)
        notifyItemRemoved(fromPosition)
        return image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.gallery_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = mData.get(position)
        Picasso.with(context)
                .load(File(image.path))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.galleryImageView)

        holder.fileNameTextView.text = image.name
        holder.fileSizeTextView.text = Utils.formatFileSizeString(image.size)
        holder.fileHashTextView.text = image.md5
    }

    override fun getItemCount() = mData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.galleryImageView)
        lateinit var galleryImageView: ImageView
        @BindView(R.id.fileNameTextView)
        lateinit var fileNameTextView: TextView
        @BindView(R.id.fileSizeTextView)
        lateinit var fileSizeTextView: TextView
        @BindView(R.id.fileHashTextView)
        lateinit var fileHashTextView: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}