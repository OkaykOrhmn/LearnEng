package com.rhmn.learneng.view.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.room.Room
import com.rhmn.learneng.R
import com.rhmn.learneng.data.AppDatabase
import com.rhmn.learneng.data.model.Day
import com.rhmn.learneng.data.model.DayResult
import com.rhmn.learneng.data.model.LayoutType
import com.rhmn.learneng.databinding.ItemDayBinding
import com.rhmn.learneng.view.fragments.HomeFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class DaysListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MyListView<Day>(context, attrs, defStyleAttr) {

    private var currentItems: List<Day> = emptyList()
    private var currentClickListener: ((Day) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    fun setup(
        items: List<Day>,
        onClick: (Day) -> Unit,
        enableScrolling: Boolean = true
    ) {
        this.currentItems = items
        this.currentClickListener = onClick
        setLayoutType(LayoutType.LINEAR_VERTICAL)
        setItems(
            layoutResId = R.layout.item_day,
            items = currentItems,
            onItemClick = { currentClickListener?.invoke(it) },
            onBindViewHolder = { binding, item, _ ->
                if (binding is ItemDayBinding) {
                    binding.days = item
                    binding.tvDay.text = "Day ${item.id}"

                    when (item.dayResult) {
                        DayResult.OPEN -> {
                            binding.tvStatusIcon.setImageResource(R.drawable.arrow_right)
                            binding.tvStatus.text = "Start"
                            val texColor = R.color.white
                            binding.tvStatus.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    texColor
                                )
                            )
                            binding.tvDay.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    texColor
                                )
                            )
                            binding.card.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.primary
                                )
                            )
                        }

                        DayResult.LOCK -> {
                            binding.tvStatusIcon.setImageResource(R.drawable.baseline_download_for_offline_24)
                            binding.tvStatus.text = "Download"
                            val texColor = R.color.black
                            binding.tvDay.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    texColor
                                )
                            )
                            binding.tvStatus.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    texColor
                                )
                            )
                            binding.card.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.white
                                )
                            )
                        }

                        DayResult.SUCCESS -> {
                            binding.tvStatusIcon.setImageResource(R.drawable.tick)
                            binding.tvStatus.text = "Complete"
                            binding.tvStatus.setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.success
                                )
                            )
                            binding.card.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.successBack
                                )
                            )
                        }
                    }
                }
            },
            enableScrolling = enableScrolling
        )
    }

    override fun updateItems(newItems: List<Day>) {
        this.currentItems = newItems
        super.updateItems(newItems)
    }

}