package com.rhmn.learneng.view.components

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.rhmn.learneng.data.model.LayoutType


open class MyListView<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var listAdapter: ListAdapter<T>? = null
    private var layoutType: LayoutType = LayoutType.LINEAR_VERTICAL
    private var gridSpanCount: Int = 10

    init {
        setLayoutType(LayoutType.LINEAR_VERTICAL)
        setHasFixedSize(true)
    }

    fun setLayoutType(type: LayoutType, spanCount: Int = 10) {
        this.layoutType = type
        this.gridSpanCount = spanCount
        layoutManager = when (type) {
            LayoutType.LINEAR_VERTICAL -> LinearLayoutManager(context)
            LayoutType.LINEAR_HORIZONTAL -> LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }

            LayoutType.GRID -> GridLayoutManager(context, spanCount)
            LayoutType.FLEXBOX -> FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.CENTER
            }
        }
    }

    fun setItems(
        @LayoutRes layoutResId: Int,
        items: List<T>,
        onItemClick: (T) -> Unit,
        onBindViewHolder: (ViewDataBinding, T, Int) -> Unit,
        enableScrolling: Boolean = true
    ) {
        Log.d("MyListView", "Setting items with layoutResId: $layoutResId")
        listAdapter = ListAdapter(layoutResId, items, onItemClick, onBindViewHolder)
        adapter = listAdapter
        isNestedScrollingEnabled = enableScrolling
    }

    open fun updateItems(newItems: List<T>) {
        listAdapter?.updateItems(newItems)
    }
}

class ListAdapter<T>(
    @LayoutRes private val layoutResId: Int,
    private var items: List<T>,
    private val onItemClick: (T) -> Unit,
    private val onBindViewHolder: (ViewDataBinding, T, Int) -> Unit
) : RecyclerView.Adapter<ListViewHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<T> {
        Log.d("ListAdapter", "Inflating layout with ID: $layoutResId")
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutResId,
            parent,
            false
        )
            ?: throw IllegalStateException("Failed to inflate layout with ID: $layoutResId. Ensure the layout exists in res/layout/ and has a <layout> tag.")
        return ListViewHolder(binding, onItemClick, onBindViewHolder)
    }

    override fun onBindViewHolder(holder: ListViewHolder<T>, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<T>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

class ListViewHolder<T>(
    private val binding: ViewDataBinding,
    private val onItemClick: (T) -> Unit,
    private val onBindViewHolder: (ViewDataBinding, T, Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T, position: Int) {
        onBindViewHolder(binding, item, position)
        binding.executePendingBindings()
        binding.root.setOnClickListener {
            onItemClick(item)
        }
    }
}