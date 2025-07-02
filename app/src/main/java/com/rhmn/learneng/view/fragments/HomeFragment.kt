package com.rhmn.learneng.view.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.rhmn.learneng.viewmodel.HomeViewModel
import com.rhmn.learneng.databinding.FragmentHomeBinding
import com.rhmn.learneng.viewmodel.DayViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val dayStatusViewModel: DayViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayStatusViewModel.initialize(requireContext())

        viewModel.fetchDaysList(requireContext())

        viewModel.daysList.observe(viewLifecycleOwner) { days ->
            binding.daysListview.setup(
                items = days,
                onClick = {},
                enableScrolling = true
            )
        }

        dayStatusViewModel.dayStatusList.observe(viewLifecycleOwner) { dayStatusList ->
            val updatedDays = viewModel.daysList.value?.map { day ->
                day.copy(dayStatus = dayStatusList.find { it.dayId == day.id } ?: day.dayStatus)
            }
            binding.daysListview.updateItems(updatedDays ?: emptyList())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}