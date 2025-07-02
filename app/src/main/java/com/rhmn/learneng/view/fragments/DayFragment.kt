package com.rhmn.learneng.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.data.model.DayType
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.databinding.FragmentDayBinding
import com.rhmn.learneng.viewmodel.DayViewModel

class DayFragment : Fragment() {
    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    private val dayStatusViewModel: DayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DayFragmentArgs.fromBundle(requireArguments())
        dayStatusViewModel.initialize(requireContext())

        dayStatusViewModel.dayStatusList.observe(viewLifecycleOwner) { dayStatusList ->
            val dayStatus = dayStatusList.find { it.dayId == args.dayId }
            if (dayStatus != null) {
                val buttons = mapOf(
                    DayStep.VOCAL to binding.vocabularyBtn,
                    DayStep.PRO to binding.pronounciationBtn,
                    DayStep.DICT to binding.dictionsBtn,
                    DayStep.GR_QUIZ to binding.grammarQuizBtn,
                    DayStep.READ to binding.readingBtn,
                    DayStep.LISTEN to binding.listeningBtn,
                    DayStep.FNL_QUIZ to binding.fnlQuizBtn
                )
                val enabledButtons = when (dayStatus.dayStep!!) {
                    DayStep.VOCAL -> listOf(DayStep.VOCAL)
                    DayStep.PRO -> listOf(DayStep.VOCAL, DayStep.PRO)
                    DayStep.DICT -> listOf(DayStep.VOCAL, DayStep.PRO, DayStep.DICT)
                    DayStep.GR_QUIZ -> listOf(DayStep.VOCAL, DayStep.PRO, DayStep.DICT,DayStep.GR_QUIZ)
                    DayStep.READ -> listOf(DayStep.VOCAL, DayStep.PRO, DayStep.DICT,DayStep.GR_QUIZ, DayStep.READ)
                    DayStep.LISTEN -> listOf(DayStep.VOCAL, DayStep.PRO, DayStep.DICT,DayStep.GR_QUIZ, DayStep.READ,DayStep.LISTEN)
                    DayStep.FNL_QUIZ -> listOf(DayStep.VOCAL, DayStep.PRO, DayStep.DICT,DayStep.GR_QUIZ, DayStep.READ,DayStep.LISTEN, DayStep.FNL_QUIZ)
                    DayStep.FINISH -> buttons.keys.toList() // All buttons enabled
                }
                buttons.forEach { (step, btn) ->
                    btn.isEnabled = step in enabledButtons
                    btn.alpha = if (btn.isEnabled) 1.0f else 0.5f
                }
            }
        }

        when (args.dayType) {
            DayType.DAY_1 -> {
                binding.day1.visibility = View.VISIBLE
                val c = args.dayId + 1
                binding.actionBar.setTitleText("Day $c")
            }
            DayType.DAY_2 -> {
                binding.day2.visibility = View.VISIBLE
                val c = args.dayId + 1
                binding.actionBar.setTitleText("Day ${c + 1}")
            }
            DayType.DAY_3 -> {
                binding.day3.visibility = View.VISIBLE
                val c = args.dayId + 1
                binding.actionBar.setTitleText("Day ${c + 2}")
            }
        }

        binding.vocabularyBtn.setOnClickListener {
            findNavController().navigate(R.id.action_day_to_wordFragment)

        }

        binding.dictionsBtn.setOnClickListener {
            if (binding.dictionsBtn.isEnabled) {
                findNavController().navigate(R.id.action_day_to_dictionsFragment)

            }
        }

        binding.pronounciationBtn.setOnClickListener {
            if (binding.pronounciationBtn.isEnabled) {
                findNavController().navigate(R.id.action_day_to_pronounciationFragment)

            }
        }

        binding.grammarBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Grammar", Toast.LENGTH_SHORT).show()
        }

        binding.grammarQuizBtn.setOnClickListener {
            val quizType = QuizType.GRAMMAR
            val action =
                DayFragmentDirections.actionDayToQuizFragment(quizType = quizType)
            findNavController().navigate(action)

        }
        binding.readingBtn.setOnClickListener {
            val quizType = QuizType.READING
            val action =
                DayFragmentDirections.actionDayToQuizFragment(quizType = quizType)
            findNavController().navigate(action)
        }

        binding.listeningBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Listening", Toast.LENGTH_SHORT).show()
        }

        binding.fnlQuizBtn.setOnClickListener {
            val quizType = QuizType.FINAL
            val action =
                DayFragmentDirections.actionDayToQuizFragment(quizType = quizType)
            findNavController().navigate(action)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}