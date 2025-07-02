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

        val score = QuizFinishDialogArgs.fromBundle(requireArguments()).score!!
        val dayId = QuizFinishDialogArgs.fromBundle(requireArguments()).dayId


        binding.correctCount.text = "${score.correctCount}"
        binding.wrongCount.text = "${score.wrongCount}"
        binding.totalCount.text = "${score.totalCount}"
        binding.score.text = "${score.resultScore} %"

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