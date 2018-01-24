package com.ai.gallerytest.presentation.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ai.gallerytest.R
import com.ai.gallerytest.presentation.ViewModelFactory
import com.ai.gallerytest.presentation.model.GalleryImage
import com.ai.gallerytest.presentation.view.adapters.GalleryAdapter
import com.ai.gallerytest.presentation.viewmodel.GalleryViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.util.concurrent.TimeUnit

class GalleryFragment : BaseFragment() {

    private val disposable = CompositeDisposable()
    private val galleryObserver = Observer<ArrayList<GalleryImage>> { images ->
        if (preloading) adapter.setData(images)
    }

    private lateinit var adapter: GalleryAdapter
    private lateinit var viewModel: GalleryViewModel

    private var mListener: IGalleryFragmentInteraction? = null
    private var preloading = true
    private var scrolledUp = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IGalleryFragmentInteraction) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement IStartFragmentInteraction")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = GalleryAdapter(context)
        viewModel = ViewModelProviders.of(this, ViewModelFactory(context))
                .get(GalleryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        galleryRecyclerView.let { recycler ->
            recycler.layoutManager = GridLayoutManager(context, 2)
            recycler.setHasFixedSize(true)
            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    scrolledUp = recyclerView.canScrollVertically(-1) == false
                }
            })
            recycler.adapter = adapter
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissions()) subscribe()
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
    }

    override fun onPermissionsGranted() {
        subscribe()
    }

    override fun onPermissionsDenied() {
        unsubscribe()
    }

    private fun subscribe() {
        disposable.add(viewModel.progressProcessor
                .onBackpressureLatest()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ progressPair ->
                    mListener?.onProgress(progressPair)
                }))
        disposable.add(viewModel.removals
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ image ->
                    adapter.removeItem(image)
                }))
        disposable.add(viewModel.additionsToStart
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ image ->
                    adapter.addNewestItem(image)
                    if (scrolledUp) galleryRecyclerView.smoothScrollToPosition(0)
                }))
        disposable.add(viewModel.additionsToEnd
                .buffer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    if (preloading) {
                        preloading = false
                        adapter.setData(images)
                    } else if (images.isNotEmpty()) {
                        adapter.addOldestItems(images)
                    }
                }))
        disposable.add(viewModel.replaces
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ replacingPair ->
                    adapter.replaceItem(replacingPair.first, replacingPair.second)
                }))
        viewModel.images.observe(this, galleryObserver)
    }

    private fun unsubscribe() {
        disposable.clear()
        viewModel.images.removeObserver(galleryObserver)
    }

    interface IGalleryFragmentInteraction {
        fun onProgress(progressPair: Pair<Int, Int>)
    }

    companion object {
        fun newInstance() = GalleryFragment().also { fragment ->
            fragment.retainInstance = true
        }
    }
}