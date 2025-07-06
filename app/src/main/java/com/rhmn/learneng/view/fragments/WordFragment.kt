package com.rhmn.learneng.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.data.model.LayoutType
import com.rhmn.learneng.data.model.Vocabulary
import com.rhmn.learneng.data.model.Word
import com.rhmn.learneng.databinding.FragmentWordBinding
import com.rhmn.learneng.databinding.ItemHeaderBinding
import com.rhmn.learneng.view.components.MyListView
import com.rhmn.learneng.viewmodel.DayViewModel
import com.rhmn.learneng.viewmodel.WordViewModel

class WordFragment : Fragment() {

    private var _binding: FragmentWordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = WordFragmentArgs.fromBundle(requireArguments())
        val dayId = args.dayId

        viewModel.fetchVocabularyList(requireContext(), dayId)

        viewModel.vocabularyList.observe(viewLifecycleOwner) { vocabularies ->
            if (!vocabularies.isNullOrEmpty()) {
                showLoading(false)
                showWord(viewModel.wordId.value ?: 0)
            }
        }

        viewModel.wordId.observe(viewLifecycleOwner) { wordId ->
            if (viewModel.vocabularyList.value.isNullOrEmpty()) return@observe
            showWord(wordId)
        }

        binding.showWordMeaningBtn.setOnClickListener {
            toggleVisibility(
                binding.meaningText,
                binding.showWordMeaningBtn,
                "معنی لغت",
                "پنهان کردن"
            )
        }

        binding.showExamplesMeaningBtn.setOnClickListener {
            toggleVisibility(
                binding.translateExamples,
                binding.showExamplesMeaningBtn,
                "معنی مثال ها",
                "پنهان کردن"
            )
        }

        binding.pervBtn.setOnClickListener {
            viewModel.decreaseId()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.increaseId()
        }
    }

    private fun showWord(wordId: Int) {
        val vocabularies = viewModel.vocabularyList.value ?: return
        val word = vocabularies.getOrNull(wordId) ?: return

        binding.meaningText.visibility = View.GONE
        binding.translateExamples.visibility = View.GONE
        binding.showWordMeaningBtn.text = "معنی لغت"
        binding.showExamplesMeaningBtn.text = "معنی مثال ها"

        binding.wordText.text = word.word.sentence
        binding.meaningText.text = word.word.translation

        val examples = binding.examples as MyListView<Word>
        examples.setLayoutType(LayoutType.LINEAR_VERTICAL)
        examples.setItems(
            layoutResId = R.layout.item_header,
            items = word.examples,
            onItemClick = {},
            onBindViewHolder = { binding, item, _ ->
                if (binding is ItemHeaderBinding) {
                    binding.text.text = item.sentence
                }
            },
            enableScrolling = false
        )

        val examplesTranslate = binding.translateExamples as MyListView<Word>
        examplesTranslate.setLayoutType(LayoutType.LINEAR_VERTICAL)
        examplesTranslate.setItems(
            layoutResId = R.layout.item_header,
            items = word.examples,
            onItemClick = {},
            onBindViewHolder = { binding, item, _ ->
                if (binding is ItemHeaderBinding) {
                    binding.text.text = item.translation
                }
            },
            enableScrolling = false
        )

        binding.pervBtn.visibility = if (wordId > 0) View.VISIBLE else View.GONE
        binding.nextBtn.visibility = if (wordId < vocabularies.size - 1) View.VISIBLE else View.GONE
    }


    private fun toggleVisibility(view: View, button: View, showText: String, hideText: String) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
            if (button is android.widget.Button) button.text = showText
        } else {
            view.visibility = View.VISIBLE
            if (button is android.widget.Button) button.text = hideText
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainContainer.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}