package com.rhmn.learneng.view.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.LayoutType
import com.rhmn.learneng.databinding.NumericItemBinding

class NumericListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MyListView<Triple<Int, Boolean, Boolean?>>(context, attrs, defStyleAttr) {


    private var currentItems: List<Triple<Int, Boolean, Boolean?>> = emptyList()
    private var currentClickListener: ((Triple<Int, Boolean, Boolean?>) -> Unit)? = null

    @SuppressLint("SetTextI18n", "ResourceType")
    fun setup(
        items: List<Triple<Int, Boolean, Boolean?>>,
        onClick: (Triple<Int, Boolean, Boolean?>) -> Unit,
        enableScrolling: Boolean = true
    ) {

        currentItems = items
        currentClickListener = onClick
        setLayoutType(LayoutType.FLEXBOX)
        setItems(
            layoutResId = R.layout.numeric_item,
            items = currentItems,
            onItemClick = { currentClickListener?.invoke(it) },
            onBindViewHolder = { binding, item, _ ->
                if (binding is NumericItemBinding) {
                    binding.numberText.text = "${item.first + 1}"
                    val ctx = binding.root.context
                    if (item.second) {
                        binding.number.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.secondary))
                        binding.numberText.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                    } else if (item.third == null) {
                        binding.number.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.failBack))
                        binding.numberText.setTextColor(ContextCompat.getColor(ctx, R.color.fail))
                    }else if (item.third!!) {
                        binding.number.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.successBack))
                        binding.numberText.setTextColor(ContextCompat.getColor(ctx, R.color.success))
                    } else  {
                        binding.number.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
                        binding.numberText.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    }
                }
            },
            enableScrolling = enableScrolling
        )
    }


    override fun updateItems(newItems: List<Triple<Int, Boolean, Boolean?>>) {
        this.currentItems = newItems
        super.updateItems(newItems)
    }
}
