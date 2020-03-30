package com.ai.gallerytest.presentation.common

import androidx.recyclerview.widget.RecyclerView

abstract class BaseSupplementingAdapter<T, H : RecyclerView.ViewHolder> : RecyclerView.Adapter<H>() {
    protected var items = ArrayList<T>()

    override fun getItemCount() = items.size

    fun setData(list: List<T>, force: Boolean = false) {
        if (force) {
            items.clear()
            items.addAll(list)
            notifyDataSetChanged()
        } else {
            applyAndAnimateRemovals(list)
            applyAndAnimateAdditions(list)
            applyAndAnimateMovedItems(list)
        }
    }

    fun removeItem(position: Int): T? {
        if (position < 0) {
            return null
        }
        val message = items.removeAt(position)
        notifyItemRemoved(position)
        return message
    }

    fun addItem(index: Int, message: T) {
        val position = Math.min(index, items.size)
        items.add(position, message)
        notifyItemInserted(position)
    }

    fun moveItem(fromIndex: Int, toIndex: Int) {
        val message = items.removeAt(fromIndex)
        val toPosition = Math.min(toIndex, items.size)
        items.add(toPosition, message)
        notifyItemMoved(fromIndex, toPosition)
    }

    private fun applyAndAnimateRemovals(newMessages: List<T>) {
        for (i in items.indices.reversed()) {
            val message = items[i]
            if (!newMessages.contains(message)) removeItem(i)
        }
    }

    private fun applyAndAnimateAdditions(newMessages: List<T>) {
        for (i in newMessages.indices) {
            val message = newMessages[i]
            if (!items.contains(message)) addItem(i, message)
        }
    }

    private fun applyAndAnimateMovedItems(newMessages: List<T>) {
        for (i in newMessages.indices.reversed()) {
            val message = newMessages[i]
            val fromPosition = items.indexOf(message)
            if (fromPosition >= 0 && fromPosition != i) {
                moveItem(fromPosition, i)
            }
        }
    }
}