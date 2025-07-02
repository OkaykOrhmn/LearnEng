package com.rhmn.learneng.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.databinding.DialogQuizFinishBinding
import com.rhmn.learneng.viewmodel.DayViewModel

class QuizFinishDialog : DialogFragment() {

    private var _binding: DialogQuizFinishBinding? = null
    private val binding get() = _binding!!

    private val dayStatusViewModel: DayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogQuizFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quizType = QuizFinishDialogArgs.fromBundle(requireArguments()).quizType
        val dayId = QuizFinishDialogArgs.fromBundle(requireArguments()).dayId

        dayStatusViewModel.initialize(requireContext()) // Initialize DayStatusViewModel

        val dayStatus =  dayStatusViewModel.dayStatusList.value!!
        val list : List<Boolean> = when (quizType) {
            QuizType.READING -> {
                dayStatus[dayId].readingQuizResult!!
            }

            QuizType.GRAMMAR -> {
                dayStatus[dayId].grammarQuizResult!!
            }

            QuizType.FINAL -> {
                dayStatus[dayId].finalQuizResult!!
            }
        }

        val correctCount = list.count { it }
        val wrongCount = list.count { !it }

        binding.correctCount.text = "$correctCount"
        binding.wrongCount.text = "$wrongCount"
        binding.totalCount.text = "${list.size}"

        val score = (correctCount * 100 ) / list.size
        binding.score.text = "$score %"

        binding.done.setOnClickListener {
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}