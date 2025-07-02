package com.rhmn.learneng.view.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.DayResult
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.data.model.DayType
import com.rhmn.learneng.data.model.Days
import com.rhmn.learneng.data.model.LayoutType
import com.rhmn.learneng.databinding.ItemDayBinding
import com.rhmn.learneng.view.fragments.HomeFragmentDirections


class DaysListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MyListView<Days>(context, attrs, defStyleAttr) {


    private var currentItems: List<Days> = emptyList()
    private var currentClickListener: ((Days) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    fun setup(
        items: List<Days>, onClick: (Days) -> Unit, enableScrolling: Boolean = true
    ) {

        currentItems = items
        currentClickListener = onClick
        setLayoutType(LayoutType.LINEAR_VERTICAL)
        setItems(
            layoutResId = R.layout.item_day,
            items = currentItems,
            onItemClick = { currentClickListener?.invoke(it) },
            onBindViewHolder = { binding, item, _ ->
                if (binding is ItemDayBinding) {
                    binding.days = item

                    val (itemType1, itemType2, itemType3) = when (item.dayStatus.dayStep) {
                        DayStep.FINISH -> Triple(DayResult.SUCCESS, DayResult.SUCCESS, DayResult.SUCCESS)
                        DayStep.VOCAL, DayStep.PRO, DayStep.DICT -> Triple(DayResult.OPEN, DayResult.LOCK, DayResult.LOCK)
                        DayStep.GR_QUIZ, DayStep.READ -> Triple(DayResult.SUCCESS, DayResult.OPEN, DayResult.LOCK)
                        DayStep.LISTEN, DayStep.FNL_QUIZ -> Triple(DayResult.SUCCESS, DayResult.SUCCESS, DayResult.OPEN)
                        else ->  Triple(DayResult.LOCK, DayResult.LOCK, DayResult.LOCK)
                    }
                    setDayStatus(
                        itemType1,
                        binding.tvStatus1,
                        binding.tvStatusIcon1,
                        binding.tvDay1,
                        binding.day1
                    )
                    setDayStatus(
                        itemType2,
                        binding.tvStatus2,
                        binding.tvStatusIcon2,
                        binding.tvDay2,
                        binding.day2
                    )
                    setDayStatus(
                        itemType3,
                        binding.tvStatus3,
                        binding.tvStatusIcon3,
                        binding.tvDay3,
                        binding.day3
                    )

                    binding.day1.setOnClickListener {
                        if (itemType1 != DayResult.LOCK) {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToDayOneFragment(dayId = item.id, dayType = DayType.DAY_1)
                            findNavController().navigate(action)
                        }

                    }
                    binding.day2.setOnClickListener {
                        if (itemType2 != DayResult.LOCK) {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToDayOneFragment(dayId = item.id, dayType = DayType.DAY_2)
                            findNavController().navigate(action)
                        }


                    }
                    binding.day3.setOnClickListener {
                        if (itemType3 != DayResult.LOCK) {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToDayOneFragment(dayId = item.id, dayType = DayType.DAY_3)
                            findNavController().navigate(action)
                        }


                    }
                    val c = (item.id + 1) * 3;
                    binding.tvDay1.text = "Day ${c - 2}"
                    binding.tvDay2.text = "Day ${c - 1}"
                    binding.tvDay3.text = "Day $c"
                }
            },
            enableScrolling = enableScrolling
        )
    }


    fun setDayStatus(
        itemType: DayResult,
        statusTextView: TextView,
        statusIconImageView: ImageView,
        dayText: TextView,
        dayView: CardView
    ) {
        val text: String
        val icon: Int
        val textColor: Int
        val backColor: Int

        when (itemType) {
            DayResult.SUCCESS -> {
                text = "Done"
                icon = R.drawable.tick
                textColor = R.color.success
                backColor = R.color.successBack
            }

            DayResult.FAIL -> {
                text = "Missed"
                icon = R.drawable.close
                textColor = R.color.fail
                backColor = R.color.failBack
            }

            DayResult.LOCK -> {
                text = ""
                statusTextView.visibility = View.GONE
                icon = R.drawable.lock
                textColor = R.color.darkGray
                backColor = R.color.white

                val params = statusIconImageView.layoutParams as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.CENTER_IN_PARENT)
                params.addRule(RelativeLayout.ALIGN_PARENT_END)
                params.addRule(RelativeLayout.CENTER_VERTICAL)
                statusIconImageView.layoutParams = params
            }

            DayResult.OPEN -> {
                text = "Start"
                icon = 0
                textColor = R.color.white
                backColor = R.color.primary
            }
        }


        statusTextView.text = text
        statusTextView.setTextColor(ContextCompat.getColor(dayView.context, textColor))
        dayText.setTextColor(ContextCompat.getColor(dayView.context, textColor))
        dayView.setCardBackgroundColor(ContextCompat.getColor(dayView.context, backColor))

        if (icon != 0) {
            statusIconImageView.setImageResource(icon)

        } else {
            statusIconImageView.visibility = View.GONE
        }
    }

    override fun updateItems(newItems: List<Days>) {
        this.currentItems = newItems
        super.updateItems(newItems)
    }
}
