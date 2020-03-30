package com.ai.gallerytest.presentation.screens.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.gallerytest.R
import com.ai.gallerytest.data.GalleryImage
import com.ai.gallerytest.data.IGalleryRepository
import com.ai.gallerytest.presentation.common.getString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.util.*


class GalleryViewModel(private val galleryRepository: IGalleryRepository) : ViewModel() {

    private val _imagesList = LinkedList<GalleryImage>()
    private val _images = MutableLiveData<List<GalleryImage>>().apply { value = _imagesList }
    private val _progress = MutableLiveData<String>().apply { value = "" }

    val images: LiveData<List<GalleryImage>> = _images

    fun observeGallery() {
        val channel =
                galleryRepository.loadImages(viewModelScope, _images.value?.size ?: 0)
        viewModelScope.launch(Dispatchers.Default) {
            channel.consumeEach { value ->
                _imagesList.addLast(value.image)
                _images.postValue(_imagesList)
                val size = value.size
                val loaded = value.loaded
                val progress =
                        if (size != 0) loaded.toDouble() * 100 / size.toDouble() else 100.0
                _progress.postValue(String.format(getString(R.string.progress_format), progress))
            }
        }
    }
}