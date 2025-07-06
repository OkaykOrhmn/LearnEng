package com.rhmn.learneng.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.R
import com.rhmn.learneng.databinding.FragmentDictionsBinding
import com.rhmn.learneng.viewmodel.DictionsViewModel

class DictionsFragment : Fragment() {

    private var _binding: FragmentDictionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DictionsViewModel by viewModels()

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

        val dayId = DictionsFragmentArgs.fromBundle(requireArguments()).dayId
        viewModel.fetchDictionList(requireContext(), dayId)

        setupObservers()
        setupClicks()
    }

    private fun setupObservers() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
            if (!isLoading) {
                updateLists()
//                updateNavigationButtons(0)
                viewModel.selectedDictionLetters.value?.let { it1 ->
                    binding.selectedDictionsListView.setup(
                        it1,
                        layout = R.layout.item_word,
                        onClick = { item ->
                            viewModel.removeFromSelectedLetters(item)
                            viewModel.addToUnselectedLetters(item)
                        },
                        enableScrolling = false
                    )
                }
                viewModel.unselectedDictionLetters.value?.let { it1 ->
                    binding.unselectedDictionsListView.setup(
                        it1,
                        onClick = { item ->
                            viewModel.removeFromUnselectedLetters(item)
                            viewModel.addToSelectedLetters(item)
                        },
                        enableScrolling = false
                    )

                }
            }
        }

        viewModel.selectedDictionLetters.observe(viewLifecycleOwner) {
            binding.selectedDictionsListView.updateItems(it)
        }
        viewModel.unselectedDictionLetters.observe(viewLifecycleOwner) {
            binding.unselectedDictionsListView.updateItems(it)
        }

        viewModel.dictionId.observe(viewLifecycleOwner) { id ->
            updateLists()
//            updateNavigationButtons(id)
        }
    }

    private fun setupClicks() {
        binding.checkBtn.setOnClickListener {
            val selectedWord = viewModel.selectedDictionLetters.value?.joinToString("") ?: ""
            val correctWord =
                viewModel.dictionList.value?.get(viewModel.dictionId.value ?: 0)?.word?.sentence
                    ?: ""

            if (selectedWord == correctWord) {
                Toast.makeText(context, "درسته", Toast.LENGTH_SHORT).show()
                if ((viewModel.dictionId.value ?: 0) < (viewModel.dictionList.value?.size
                        ?: 0) - 1
                ) {
                    viewModel.increaseId()
                }else{
                    findNavController().popBackStack()
                }
            } else {
                Toast.makeText(context, "اشتباهه", Toast.LENGTH_SHORT).show()
            }
        }

//        binding.pervBtn.setOnClickListener {
//            viewModel.decreaseId()
//        }
//
//        binding.nextBtn.setOnClickListener {
//            viewModel.increaseId()
//        }

    }

    private fun updateLists() {
        viewModel.initializeLetterLists()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainContainer.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

//    private fun updateNavigationButtons(id: Int) {
//        val total = viewModel.dictionList.value?.size ?: 0
//        binding.pervBtn.visibility = if (id <= 0) View.GONE else View.VISIBLE
//        binding.nextBtn.visibility = if (id >= total - 1) View.GONE else View.VISIBLE
//    }
}
