package com.rhmn.learneng.view.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import android.view.View
import androidx.navigation.findNavController
import com.rhmn.learneng.R
import com.rhmn.learneng.databinding.ToolBarBinding

class PrimaryToolBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    private lateinit var binding: ToolBarBinding

    init {
        try {
            binding = ToolBarBinding.inflate(
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater,
                this,
                true
            )

            binding.backButton.setOnClickListener{
                findNavController().popBackStack()
            }

            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PrimaryToolBar,
                0,
                0
            ).apply {
                try {
                    val title = getString(R.styleable.PrimaryToolBar_titleText)
                    binding.title.text = title ?: context.getString(R.string.app_name)

                    val backEnabled = getBoolean(R.styleable.PrimaryToolBar_backEnabled, false)
                    binding.backButton.visibility = if (backEnabled) View.VISIBLE else View.GONE
                } finally {
                    recycle()
                }
            }
        } catch (e: Exception) {
            // Log or handle preview-specific errors silently to avoid breaking the preview
        }
    }

    fun setTitleText(title: String) {
        binding.title.text = title
    }

    fun setBackEnabled(enabled: Boolean) {
        binding.backButton.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun setOnBackClickListener(listener: OnClickListener) {
        binding.backButton.setOnClickListener(listener)
    }
}