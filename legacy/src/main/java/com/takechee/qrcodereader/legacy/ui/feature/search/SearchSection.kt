package com.takechee.qrcodereader.legacy.ui.feature.search

import android.view.View
import com.takechee.qrcodereader.legacy.R
import com.takechee.qrcodereader.legacy.databinding.ItemSearchBinding
import com.takechee.qrcodereader.model.Content
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.viewbinding.BindableItem

class SearchSection(private val eventListener: SearchEventListener) : Section() {
    fun update(state: SearchState) {
        when (state) {
            SearchState.Empty -> updateEmpty()
            SearchState.NoResult -> updateNoResult()
            is SearchState.Success -> updateSuccess(state.contents)
        }
    }

    private fun updateEmpty() {
        removePlaceholder()
        update(emptyList())
    }

    private fun updateNoResult() {
        setPlaceholder(SearchDoesNotExistItem)
        update(emptyList())
    }

    private fun updateSuccess(contents: List<Content>) {
        val list = mutableListOf<Item<*>>()
        contents.mapTo(list) { content -> SearchItem(content, eventListener) }
        removePlaceholder()
        update(list)
    }
}


// =============================================================================================
//
// EmptyItem
//
// =============================================================================================
private object SearchDoesNotExistItem : Item<GroupieViewHolder>(Long.MIN_VALUE) {
    override fun getLayout(): Int = R.layout.item_search_does_not_exist
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {}
}


// =============================================================================================
//
// Item
//
// =============================================================================================
data class SearchItem(
    val content: Content,
    private val eventListener: SearchEventListener
) : BindableItem<ItemSearchBinding>(content.id) {
    override fun getLayout(): Int = R.layout.item_search
    override fun initializeViewBinding(view: View) = ItemSearchBinding.bind(view)
    override fun bind(viewBinding: ItemSearchBinding, position: Int) {
        viewBinding.content = content
        viewBinding.eventListener = eventListener
    }
}