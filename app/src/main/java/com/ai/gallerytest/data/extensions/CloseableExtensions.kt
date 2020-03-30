package com.ai.gallerytest.data.extensions

import java.io.Closeable

fun Closeable?.closeQuietly() {
    if (this != null) {
        try {
            close()
        } catch (rethrown: RuntimeException) {
            throw rethrown
        } catch (ignored: Exception) {
        }
    }
}