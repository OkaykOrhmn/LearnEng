package com.rhmn.learneng.view.components

import android.content.Context
import android.util.AttributeSet
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.LayoutType
import com.rhmn.learneng.databinding.ItemDictationWordBinding
import com.rhmn.learneng.databinding.ItemWordBinding

class DictionsListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MyListView<String>(context, attrs, defStyleAttr) {

    private var currentItems: List<String> = emptyList()
    private var currentClickListener: ((String) -> Unit)? = null

    fun setup(
        items: List<String>,
        onClick: (String) -> Unit,
        layout: Int = R.layout.item_dictation_word,
        enableScrolling: Boolean = true
    ) {
        require(layout == R.layout.item_dictation_word || layout == R.layout.item_word) {
            "Layout must be either R.layout.item_dictation_word or R.layout.item_word"
        }

        currentItems = items
        currentClickListener = onClick
        setLayoutType(LayoutType.FLEXBOX)
        setItems(
            layoutResId = layout,
            items = currentItems,
            onItemClick = { currentClickListener?.invoke(it) },
            onBindViewHolder = { binding, item, _ ->
                when (binding) {
                    is ItemDictationWordBinding -> {
                        binding.dictation = item
                        binding.wordText.text = item
                    }
                    is ItemWordBinding -> {
                        binding.dictation = item
                        binding.wordText.text = item
                    }
                }
            },
            enableScrolling = enableScrolling
        )
    }

    override fun updateItems(newItems: List<String>) {
        this.currentItems = newItems
        super.updateItems(newItems)
    }
}