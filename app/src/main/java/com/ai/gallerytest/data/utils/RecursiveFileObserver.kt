package com.ai.gallerytest.data.utils

import android.os.FileObserver
import java.io.File
import java.util.*
import kotlin.collections.HashMap

abstract class RecursiveFileObserver(val root: String, val mask: Int) : FileObserver(root, mask) {

    private val mObservers = HashMap<String, SingleFileObserver>()

    fun startWatching(path: String?) {
        mObservers.putAll(appendDirectory(path))
    }

    fun stopWatching(path: String?) {
        mObservers.remove(path)?.stopWatching()
    }

    override fun startWatching() {
        mObservers.clear()
        startWatching(root)
    }

    override fun stopWatching() {
        for (sfo in mObservers.values) {
            sfo.stopWatching()
        }
        mObservers.clear()
    }

    private fun appendDirectory(root: String?): Map<String, SingleFileObserver> {
        val result = HashMap<String, SingleFileObserver>()
        val stack = Stack<String>()
        stack.push(root)

        while (!stack.isEmpty()) {
            val parent = stack.pop()
            if (mObservers.containsKey(parent)) continue
            result.put(parent, SingleFileObserver(parent, mask))
            val path = File(parent)
            val files = path.listFiles() ?: continue
            for (f in files) {
                if (f.isDirectory && f.name != "." && f.name != "src/androidTest") {
                    stack.push(f.path)
                }
            }
        }

        for (sfo in result.values) {
            sfo.startWatching()
        }
        return result
    }

    inner class SingleFileObserver(val root: String, mask: Int) : FileObserver(root, mask) {

        override fun onEvent(eventId: Int, path: String?) {
            path?.let {
                this@RecursiveFileObserver.onEvent(eventId, root + "/" + path)
            }
        }
    }
}