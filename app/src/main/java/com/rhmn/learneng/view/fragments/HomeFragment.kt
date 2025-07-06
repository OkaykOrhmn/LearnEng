package com.rhmn.learneng.view.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.data.model.DayResult
import com.rhmn.learneng.data.model.DayType
import com.rhmn.learneng.viewmodel.HomeViewModel
import com.rhmn.learneng.databinding.FragmentHomeBinding
import com.rhmn.learneng.viewmodel.DayViewModel
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchDaysList(requireContext())

        viewModel.daysList.observe(viewLifecycleOwner) { days ->
            binding.daysListview.updateItems(days)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                binding.loading.visibility = View.GONE
                binding.daysListview.visibility = View.VISIBLE
                binding.daysListview.setup(
                    items = viewModel.daysList.value!!,
                    onClick = { day ->
                        if (day.dayResult == DayResult.LOCK) {
                            viewModel.fetchDayData(requireContext(), day.id)
                        } else {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToDayOneFragment(dayId = day.id)
                            findNavController().navigate(action)
                        }
                    },
                    enableScrolling = true
                )
            }
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