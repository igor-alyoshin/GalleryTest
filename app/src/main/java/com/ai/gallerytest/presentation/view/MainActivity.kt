package com.ai.gallerytest.presentation.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ai.gallerytest.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StartFragment.IStartFragmentInteraction, GalleryFragment.IGalleryFragmentInteraction {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportActionBar?.setTitle(R.string.app_name)
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.contentFragment, StartFragment.newInstance(), TAG_START_FRAGMENT)
                    .commit()
        } else {
            val galleryVisible = supportFragmentManager.findFragmentByTag(TAG_GALLERY_FRAGMENT) != null
            supportActionBar?.setDisplayHomeAsUpEnabled(galleryVisible)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStartGalleryButtonClick() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFragment, GalleryFragment.newInstance(), TAG_GALLERY_FRAGMENT)
                .addToBackStack(TAG_GALLERY_FRAGMENT)
                .commit()
    }

    override fun onProgress(progressPair: Pair<Int, Int>) {
        val loaded = progressPair.first
        val size = progressPair.second
        val progress = if (size != 0) loaded.toDouble() * 100 / size.toDouble() else 100.0
        supportActionBar?.title = String.format(getString(R.string.progress_format), progress)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setTitle(R.string.app_name)
        }
    }

    companion object {
        val TAG_START_FRAGMENT = "TAG_START_FRAGMENT"
        val TAG_GALLERY_FRAGMENT = "TAG_GALLERY_FRAGMENT"
    }
}