package com.ai.gallerytest.presentation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.FileObserver
import android.os.FileObserver.*
import android.util.Log
import com.ai.gallerytest.data.Event
import com.ai.gallerytest.domain.LoadImagesUseCase
import com.ai.gallerytest.domain.UpdateImagesUseCase
import com.ai.gallerytest.presentation.model.GalleryImage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers


class GalleryViewModel(val loadImagesUseCase: LoadImagesUseCase, val updateImagesUseCase: UpdateImagesUseCase) : ViewModel() {

    private val TAG = GalleryViewModel::class.java.simpleName

    val progressProcessor = BehaviorProcessor.create<Pair<Int, Int>>()
    val removals = PublishProcessor.create<GalleryImage>()
    val replaces = PublishProcessor.create<Pair<GalleryImage, GalleryImage>>()
    val additionsToStart = PublishProcessor.create<GalleryImage>()
    val additionsToEnd = PublishProcessor.create<GalleryImage>()
    val images = FileObserverLiveData()

    inner class FileObserverLiveData : LiveData<ArrayList<GalleryImage>>() {

        private val disposable = CompositeDisposable()

        init {
            value = ArrayList()
        }

        override fun onActive() {
            disposable.add(loadImagesUseCase.execute(value ?: ArrayList())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(
                            { wrapper ->
                                postValue(value?.also { data ->
                                    data.add(wrapper.image)
                                })
                                additionsToEnd.onNext(wrapper.image)
                                progressProcessor.onNext(Pair(wrapper.loaded, wrapper.size))
                            },
                            { error ->
                                Log.e(TAG, "Unable to get all images through by ContentResolver", error)
                            },
                            {
                                val size = value?.size ?: 0
                                progressProcessor.onNext(Pair(size, size))
                            }))
            disposable.add(updateImagesUseCase.execute()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(object : Consumer<Event> {

                        private var replacingImage: GalleryImage? = null

                        override fun accept(eventStructure: Event) {
                            val eventId = eventStructure.eventId
                            val image = eventStructure.image
                            when (eventId and FileObserver.ALL_EVENTS) {
                                MOVED_TO -> {
                                    postValue(value?.also { data ->
                                        data.add(0, image)
                                    })
                                    replacingImage?.let { source ->
                                        replaces.onNext(Pair(source, image))
                                        replacingImage = null
                                    } ?: let {
                                        additionsToStart.onNext(image)
                                    }
                                }
                                MOVED_FROM -> {
                                    postValue(value?.also { data ->
                                        data.remove(image)
                                    })
                                    replacingImage = image
                                }
                                CREATE -> {
                                    postValue(value?.also { data ->
                                        data.add(0, image)
                                    })
                                    additionsToStart.onNext(image)
                                }
                                DELETE -> {
                                    postValue(value?.also { data ->
                                        data.remove(image)
                                    })
                                    removals.onNext(image)
                                }
                                else -> {
                                    Log.w(TAG, "EVENT ${eventId} path=${image.path}")
                                }
                            }
                        }
                    }))
        }

        override fun onInactive() {
            disposable.clear()
        }
    }
}