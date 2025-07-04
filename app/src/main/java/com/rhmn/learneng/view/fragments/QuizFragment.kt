package com.rhmn.learneng.view.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.data.model.OptionStatus
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.data.model.Score
import com.rhmn.learneng.databinding.FragmentQuizBinding
import com.rhmn.learneng.view.QuizFinishDialogDirections
import com.rhmn.learneng.viewmodel.DayViewModel
import com.rhmn.learneng.viewmodel.QuizViewModel
import kotlinx.coroutines.*

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()
    private val dayStatusViewModel: DayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dayId = parentFragment?.arguments?.let { args ->
            DayFragmentArgs.fromBundle(args).dayId
        } ?: 0
        val quizType = QuizFragmentArgs.fromBundle(requireArguments()).quizType
        viewModel.dayId = dayId

        dayStatusViewModel.initialize(requireContext())

        when (quizType) {
            QuizType.READING -> {
                binding.actionBar.setTitleText("Reading")
            }

            QuizType.GRAMMAR -> {
                binding.actionBar.setTitleText("Grammar quiz")
            }

            QuizType.FINAL -> {
                binding.actionBar.setTitleText("Final quiz")
            }
        }

        viewModel.fetchQuiz(requireContext(), quizType)

        viewModel.readingItem.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.readingSection.visibility = View.GONE
                (binding.mainContainer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = (64f  * resources.displayMetrics.density).toInt()
                return@observe
            }
            binding.readingTitle.text = it.title
            binding.readingText.text = it.text
            binding.translateText.text = it.translation

        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (!it) {
                binding.stepView.setCurrentStep(1)
                binding.stepView.setStepCount(viewModel.quizList.value!!.size)

            }
        }


        viewModel.quizList.observe(viewLifecycleOwner) {
            if (viewModel.quizList.value!!.all { q -> q.userAnswer != null }) {
                val cAnswers = viewModel.quizList.value!!.count { q -> q.answer == q.userAnswer }
                val wAnswers = viewModel.quizList.value!!.count { q -> q.answer != q.userAnswer }
                val total = viewModel.quizList.value!!.size
                val score = (cAnswers * 100) / total
                val action =
                    QuizFinishDialogDirections.actionToQuizFinishDialog(
                        score = Score(
                            correctCount = cAnswers,
                            wrongCount = wAnswers,
                            totalCount = total,
                            resultScore = score
                        ),
                        dayId = dayId
                    )

                findNavController().navigate(action)
            }
        }



        binding.pervBtn.setOnClickListener {
            if (viewModel.quizId.value != 0) {
                viewModel.decreaseId()
            }
        }

        binding.nextBtn.setOnClickListener {
            if (viewModel.quizId.value != viewModel.quizList.value!!.size - 1) {
                viewModel.increaseId()
            }
        }

        binding.translateBtn.setOnClickListener {
            if (binding.translateText.visibility == View.GONE) {
                binding.translateText.visibility = View.VISIBLE
                binding.translateBtn.text = "پنهان کردن"
            } else {
                binding.translateText.visibility = View.GONE
                binding.translateBtn.text = "معنی متن"

            }
        }

        viewModel.quizId.observe(viewLifecycleOwner) { id ->
            binding.stepView.setCurrentStep(id + 1)
            val item = viewModel.quizList.value!![id]
            binding.questionText.text = item.question
            val items =
                item.options.mapIndexed { index, opt ->
                    var optionStatus = OptionStatus.INIT
                    if(item.userAnswer!=null){
                        if(opt == item.answer){
                            optionStatus = OptionStatus.CORRECT
                        }
                        if(opt == item.userAnswer && item.userAnswer !=item.answer){
                            optionStatus = OptionStatus.WRONG
                        }
                    }
                    Triple(index, opt, optionStatus)
                }
                    .toList()
            binding.optionsListView.setup(
                items = items,
                onClick = { i ->
                    binding.optionsListView.setEnableClick(false)
                    viewModel.updateQuizField(i.second)
                    onItemClick()

                },
            )
            binding.optionsListView.setEnableClick(item.userAnswer == null)
            binding.pervBtn.visibility =
                if (id == 0) View.GONE else View.VISIBLE
            binding.nextBtn.visibility =
                if (id == viewModel.quizList.value!!.size - 1) View.GONE else View.VISIBLE
        }
    }

    fun onItemClick(){
        binding.pervBtn.isEnabled = false
        binding.nextBtn.isEnabled = false
        val item = viewModel.quizList.value!![viewModel.quizId.value!!]
        binding.questionText.text = item.question
        val items =
            item.options.mapIndexed { index, opt ->
                var optionStatus = OptionStatus.INIT
                if(item.userAnswer!=null){
                    if(opt == item.answer){
                        optionStatus = OptionStatus.CORRECT
                    }
                    if(opt == item.userAnswer && item.userAnswer !=item.answer){
                        optionStatus = OptionStatus.WRONG
                    }
                }
                Triple(index, opt, optionStatus)
            }
                .toList()
        binding.optionsListView.updateItems(items)

        showFeedback(item.userAnswer == item.answer)
        if (viewModel.quizId.value != viewModel.quizList.value!!.size - 1) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                viewModel.increaseId()
            }
        }

        binding.pervBtn.isEnabled = true
        binding.nextBtn.isEnabled = true
    }


    private fun showFeedback(isCorrect: Boolean) {
        val message = if (isCorrect) "درسته" else "اشتباهه"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
