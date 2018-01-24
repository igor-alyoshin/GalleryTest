package com.ai.gallerytest

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.os.FileObserver.CREATE
import android.os.FileObserver.DELETE
import com.ai.gallerytest.data.Event
import com.ai.gallerytest.data.GalleryLoadingProgress
import com.ai.gallerytest.data.IGalleryRepository
import com.ai.gallerytest.domain.LoadImagesUseCase
import com.ai.gallerytest.domain.UpdateImagesUseCase
import com.ai.gallerytest.presentation.model.GalleryImage
import com.ai.gallerytest.presentation.viewmodel.GalleryViewModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


class GalleryViewModelTest {

    private lateinit var viewModel: GalleryViewModel

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock lateinit var repository: IGalleryRepository
    @Mock lateinit var observer: Observer<ArrayList<GalleryImage>>

    val galleryImageMock = GalleryImage("path", "name", "md5hash1", 1)
    val galleryImageMock2 = GalleryImage("path2", "name2", "md5hash2", 1)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = GalleryViewModel(LoadImagesUseCase(repository), UpdateImagesUseCase(repository))
    }

    @Test
    fun testGetAllImagesAndProgressAction() {
        `when`(repository.getAllImages(any<ArrayList<GalleryImage>>() ?: ArrayList()))
                .thenReturn(Observable.just(GalleryLoadingProgress(1, 1, galleryImageMock)))
        `when`(repository.getImageEvents())
                .thenReturn(Flowable.empty())
        viewModel.images.observeForever(observer)
        verify(observer, times(1)).onChanged(arrayListOf(galleryImageMock))
        viewModel.progressProcessor
                .test()
                .assertNoErrors()
                .assertValue(Pair(1, 1))
    }

    @Test
    fun testGetAllImagesAndCreate() {
        `when`(repository.getAllImages(any<ArrayList<GalleryImage>>() ?: ArrayList()))
                .thenReturn(Observable.just(GalleryLoadingProgress(1, 1, galleryImageMock)))
        `when`(repository.getImageEvents())
                .thenReturn(Flowable.just(Event(CREATE, galleryImageMock2)))
        viewModel.images.observeForever(observer)
        verify(observer, times(2)).onChanged(arrayListOf(galleryImageMock2, galleryImageMock))
    }

    @Test
    fun testGetAllImagesAndCreateAndDelete() {
        `when`(repository.getAllImages(any<ArrayList<GalleryImage>>() ?: ArrayList()))
                .thenReturn(Observable.just(GalleryLoadingProgress(1, 1, galleryImageMock)))
        `when`(repository.getImageEvents())
                .thenReturn(Flowable.just(Event(CREATE, galleryImageMock2), Event(DELETE, galleryImageMock)))
        viewModel.images.observeForever(observer)
        verify(observer, times(3)).onChanged(arrayListOf(galleryImageMock2))
    }

    @Test
    fun testImageEvents() {
        `when`(repository.getAllImages(any<ArrayList<GalleryImage>>() ?: ArrayList()))
                .thenReturn(Observable.empty())
        `when`(repository.getImageEvents())
                .thenReturn(Flowable.just(Event(CREATE, galleryImageMock2)))
        viewModel.images.observeForever(observer)
        verify(observer, times(1)).onChanged(arrayListOf(galleryImageMock2))
    }
}
