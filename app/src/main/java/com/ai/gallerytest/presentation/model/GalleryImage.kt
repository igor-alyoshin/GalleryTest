package com.ai.gallerytest.presentation.model


data class GalleryImage(val path: String, val name: String, val md5: String?, val size: Long) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as GalleryImage

        return if (path != that.path) false else name == that.name
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}