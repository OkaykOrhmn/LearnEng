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
import com.rhmn.learneng.data.model.Vocal
import com.rhmn.learneng.databinding.FragmentWordBinding
import com.rhmn.learneng.databinding.ItemHeaderBinding
import com.rhmn.learneng.view.components.MyListView
import com.rhmn.learneng.viewmodel.DayViewModel
import com.rhmn.learneng.viewmodel.WordViewModel

class WordFragment : Fragment() {

    private var _binding: FragmentWordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WordViewModel by viewModels()
    private val dayStatusViewModel: DayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dayId = parentFragment?.arguments?.let { args ->
            DayFragmentArgs.fromBundle(args).dayId
        } ?: 0
        viewModel.dayId = dayId

        dayStatusViewModel.initialize(requireContext()) // Initialize DayStatusViewModel

        binding.mainContainer.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE

        viewModel.fetchVocabularyList(requireContext())

        viewModel.wordId.observe(viewLifecycleOwner) { wordId ->
            if (wordId == null) return@observe

            binding.loading.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
            binding.meaningText.visibility = View.GONE
            binding.translateExamples.visibility = View.GONE
            binding.showWordMeaningBtn.text = "معنی لغت"
            binding.showExamplesMeaningBtn.text = "معنی مثال ها"

            val word = viewModel.vocabularyList.value?.get(wordId)!!
            binding.wordText.text = word.word
            binding.meaningText.text = word.meaning

            val examples =  binding.examples as MyListView<Vocal>
            examples.setLayoutType(LayoutType.LINEAR_VERTICAL)
            examples.setItems(
                layoutResId = R.layout.item_header,
                items = word.examples!!,
                onItemClick = {  },
                onBindViewHolder = { binding, item, _ ->
                    if (binding is ItemHeaderBinding) {
                        binding.text.text = item.word
                    }
                },
                enableScrolling = false
            )

            val examplesTranslate =  binding.translateExamples as MyListView<Vocal>
            examplesTranslate.setLayoutType(LayoutType.LINEAR_VERTICAL)
            examplesTranslate.setItems(
                layoutResId = R.layout.item_header,
                items = word.examples,
                onItemClick = {  },
                onBindViewHolder = { binding, item, _ ->
                    if (binding is ItemHeaderBinding) {
                        binding.text.text = item.meaning
                    }
                },
                enableScrolling = false
            )

            binding.pervBtn.visibility = if (wordId > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.nextBtn.visibility = if (wordId < viewModel.vocabularyList.value!!.size - 1) {
                View.VISIBLE
            } else {
                View.GONE
            }

            if (wordId == viewModel.vocabularyList.value!!.size - 1) {
                dayStatusViewModel.updateDayStatus(
                    context = requireContext(),
                    dayId = dayId,
                    newDayStep = DayStep.PRO
                )
            }
        }

        binding.showWordMeaningBtn.setOnClickListener {
            if (binding.meaningText.visibility == View.VISIBLE) {
                binding.meaningText.visibility = View.GONE
                binding.showWordMeaningBtn.text = "معنی لغت"
            } else {
                binding.meaningText.visibility = View.VISIBLE
                binding.showWordMeaningBtn.text = "پنهان کردن"
            }
        }

        binding.showExamplesMeaningBtn.setOnClickListener {
            if (binding.translateExamples.visibility == View.VISIBLE) {
                binding.translateExamples.visibility = View.GONE
                binding.showExamplesMeaningBtn.text = "معنی مثال ها"
            } else {
                binding.translateExamples.visibility = View.VISIBLE
                binding.showExamplesMeaningBtn.text = "پنهان کردن"

            }
        }

        binding.pervBtn.setOnClickListener {
            viewModel.decreaseId()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.increaseId()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}