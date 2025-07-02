package com.rhmn.learneng.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.databinding.FragmentDictionsBinding
import com.rhmn.learneng.viewmodel.DayViewModel
import com.rhmn.learneng.viewmodel.DictionsViewModel

class DictionsFragment : Fragment() {

    private var _binding: FragmentDictionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DictionsViewModel by viewModels()
    private val dayStatusViewModel: DayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dayId = parentFragment?.arguments?.let { args ->
            DayFragmentArgs.fromBundle(args).dayId
        } ?: 0
        viewModel.dayId = dayId

        dayStatusViewModel.initialize(requireContext())

        viewModel.fetchDictionList(requireContext())

        viewModel.dictionList.observe(viewLifecycleOwner) { list ->
            viewModel.fillUnselectedDictionLetterList()

            updateNumList(list)

            if (list.all { it.third }) {
                dayStatusViewModel.updateDayStatus(
                    requireContext(),
                    dayId,
                    newDayStep = DayStep.GR_QUIZ
                )
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (!it) {
                val numerics = viewModel.dictionList.value!!.mapIndexed() { index, pronunciation ->
                    Triple(
                        index,
                        pronunciation.first == viewModel.dictionId.value,
                        pronunciation.third
                    )
                }
                binding.numericListView.setup(
                    numerics,
                    onClick = { item ->
                        viewModel.setId(item.first)
                    },
                )
                viewModel.selectedDictionLetterList.value?.let { it1 ->
                    binding.selectedDictionsListView.setup(
                        it1,
                        onClick = { item ->
                            viewModel.removeItemFromSelectedDictionLetterList(item)
                            viewModel.addItemToUnelectedDictionLetterList(item)
                        },
                    )
                }
                viewModel.unselectedDictionLetterList.value?.let { it1 ->
                    binding.unselectedDictionsListView.setup(
                        it1,
                        onClick = { item ->
                            viewModel.removeItemFromUnselectedDictionLetterList(item)
                            viewModel.addItemToSelectedDictionLetterList(item)
                        },
                    )
                }
            }
        }

        binding.checkBtn.setOnClickListener {
            val selectedLetters = viewModel.selectedDictionLetterList.value
            val result = selectedLetters!!.joinToString(separator = "")
            val word = viewModel.dictionList.value!![viewModel.dictionId.value!!].second

            if (result == word) {
                Toast.makeText(context, "درسته", Toast.LENGTH_SHORT).show()
                viewModel.updateDictionField(true)
                if (viewModel.dictionId.value!! < viewModel.dictionList.value!!.size - 1) {
                    viewModel.increaseId()
                    viewModel.fillUnselectedDictionLetterList()
                }
            } else {
                Toast.makeText(context, "اشتباهه", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.selectedDictionLetterList.observe(viewLifecycleOwner) {
            binding.selectedDictionsListView.updateItems(it)
        }
        viewModel.unselectedDictionLetterList.observe(viewLifecycleOwner) {
            binding.unselectedDictionsListView.updateItems(it)
        }

        binding.pervBtn.setOnClickListener {
            if (viewModel.dictionId.value != 0) {
                viewModel.decreaseId()
                viewModel.fillUnselectedDictionLetterList()
            }
        }

        binding.nextBtn.setOnClickListener {
            if (viewModel.dictionId.value != viewModel.dictionList.value!!.size - 1) {
                viewModel.increaseId()
                viewModel.fillUnselectedDictionLetterList()
            }
        }

        viewModel.dictionId.observe(viewLifecycleOwner) {
            viewModel.fillUnselectedDictionLetterList()
            updateNumList(viewModel.dictionList.value!!)

            if (it == 0) {
                binding.pervBtn.visibility = View.GONE
            } else {
                binding.pervBtn.visibility = View.VISIBLE
            }

            if (it == viewModel.dictionList.value!!.size - 1) {
                binding.nextBtn.visibility = View.GONE
            } else {
                binding.nextBtn.visibility = View.VISIBLE
            }
        }


    }

    fun updateNumList(list: List<Triple<Int, String, Boolean>>) {
        val numerics = list.mapIndexed { index, it ->
            Triple(index, it.first == viewModel.dictionId.value, it.third)
        }
        binding.numericListView.updateItems(numerics)
    }

}