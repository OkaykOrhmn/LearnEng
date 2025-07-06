package com.rhmn.learneng.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.DayResult
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.data.model.DayType
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.databinding.FragmentDayBinding
import com.rhmn.learneng.viewmodel.DayViewModel

class DayFragment : Fragment() {
    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DayViewModel by viewModels()

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
        viewModel.fetchLesson(requireContext(), args.dayId)

        binding.actionBar.setTitleText("Day ${args.dayId}")

        viewModel.lessonItem.observe(viewLifecycleOwner) {
            if (it.vocabularies != null) {
                binding.vocabularyBtn.visibility = View.VISIBLE
            }
            if (it.dictations != null) {
                binding.dictionsBtn.visibility = View.VISIBLE
            }
            if (it.pronunciations != null) {
                binding.pronounciationBtn.visibility = View.VISIBLE
            }
            if (it.grammarQuiz != null) {
                binding.grammarQuizBtn.visibility = View.VISIBLE
            }
            if (it.readings != null) {
                binding.readingBtn.visibility = View.VISIBLE
            }
            if (it.listening != null) {
                binding.listeningBtn.visibility = View.VISIBLE
            }
            if (it.finalQuiz != null) {
                binding.fnlQuizBtn.visibility = View.VISIBLE
            }
        }
        binding.vocabularyBtn.setOnClickListener {
            val action = DayFragmentDirections.actionDayToWordFragment(dayId = args.dayId)
            findNavController().navigate(action)
        }
        binding.dictionsBtn.setOnClickListener {
            val action = DayFragmentDirections.actionDayToDictionsFragment(dayId = args.dayId)
            findNavController().navigate(action)
        }

        binding.pronounciationBtn.setOnClickListener {
            val action = DayFragmentDirections.actionDayToPronounciationFragment(dayId = args.dayId)
            findNavController().navigate(action)
        }

        binding.grammarBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Grammar", Toast.LENGTH_SHORT).show()
        }

        binding.grammarQuizBtn.setOnClickListener {
            val quizType = QuizType.GRAMMAR
            val action =
                DayFragmentDirections.actionDayToQuizFragment(
                    quizType = quizType,
                    dayId = args.dayId
                )
            findNavController().navigate(action)

        }
        binding.readingBtn.setOnClickListener {
            val quizType = QuizType.READING
            val action =
                DayFragmentDirections.actionDayToQuizFragment(
                    quizType = quizType,
                    dayId = args.dayId
                )
            findNavController().navigate(action)
        }

        binding.listeningBtn.setOnClickListener {
            val action =
                DayFragmentDirections.actionDayFragmentToListeningFragment(dayId = args.dayId)
            findNavController().navigate(action)
        }

        binding.fnlQuizBtn.setOnClickListener {
            val quizType = QuizType.FINAL
            val action =
                DayFragmentDirections.actionDayToQuizFragment(
                    quizType = quizType,
                    dayId = args.dayId
                )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}