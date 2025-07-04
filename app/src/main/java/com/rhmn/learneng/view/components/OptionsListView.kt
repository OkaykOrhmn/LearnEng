package com.rhmn.learneng.view.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.LayoutType
import com.rhmn.learneng.data.model.OptionStatus
import com.rhmn.learneng.databinding.ItemDayBinding
import com.rhmn.learneng.databinding.OptionItemBinding
import com.rhmn.learneng.view.fragments.HomeFragmentDirections


class OptionsListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MyListView<Triple<Int, String, OptionStatus>>(context, attrs, defStyleAttr) {


    private var currentItems: List<Triple<Int, String, OptionStatus>> = emptyList()
    private var currentClickListener: ((Triple<Int, String, OptionStatus>) -> Unit)? = null
    private var enableClick: Boolean = true


    @SuppressLint("SetTextI18n")
    fun setup(
        items: List<Triple<Int, String, OptionStatus>>,
        onClick: (Triple<Int, String, OptionStatus>) -> Unit,
        enableScrolling: Boolean = true,
    ) {
        currentItems = items
        currentClickListener = onClick
        setLayoutType(LayoutType.LINEAR_VERTICAL)
        setItems(
            layoutResId = R.layout.option_item,
            items = currentItems,
            onItemClick = {
                if (enableClick) {
                    currentClickListener?.invoke(it)
                }
            },
            onBindViewHolder = { binding, item, _ ->
                if (binding is OptionItemBinding) {
                    binding.opt = item.second

                    binding.mainBtn.text = item.second

                    when (item.third) {
                        OptionStatus.CORRECT -> {
                            binding.mainBtn.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.white
                                )
                            )
                            binding.mainBtn.setBackgroundColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.success
                                )
                            )
                        }

                        OptionStatus.WRONG -> {
                            binding.mainBtn.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.white
                                )
                            )
                            binding.mainBtn.setBackgroundColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.fail
                                )
                            )
                        }

                        OptionStatus.INIT -> {
                            binding.mainBtn.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.black
                                )
                            )
                            binding.mainBtn.setBackgroundColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.white
                                )
                            )
                        }
                    }

                }
            },
            enableScrolling = enableScrolling
        )
    }

    fun setEnableClick(newValue: Boolean) {
        enableClick = newValue
    }

    override fun updateItems(newItems: List<Triple<Int, String, OptionStatus>>) {
        this.currentItems = newItems
        super.updateItems(newItems)
    }
}
