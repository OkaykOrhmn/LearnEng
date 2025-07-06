package com.rhmn.learneng.view.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.data.model.OptionStatus
import com.rhmn.learneng.data.model.Quiz
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

        val args = QuizFragmentArgs.fromBundle(requireArguments())
        val quizType = args.quizType
        val dayId = args.dayId


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

        viewModel.fetchQuiz(requireContext(), dayId, quizType)

        viewModel.readingItem.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.readingSection.visibility = View.GONE
                (binding.mainContainer.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    (64f * resources.displayMetrics.density).toInt()
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
                val item =
                    viewModel.quizList.value?.get(viewModel.quizId.value ?: 0) ?: return@observe
                updateUi(item)
            }
        }


        viewModel.quizList.observe(viewLifecycleOwner) {
            val item =
                viewModel.quizList.value?.get(viewModel.quizId.value ?: 0)
            if (item != null) updateUi(item)
            if (viewModel.quizList.value!!.all { q -> q.userAnswer != null }) {
                binding.againBtn.visibility = View.VISIBLE
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

        binding.againBtn.setOnClickListener {
            binding.againBtn.visibility = View.GONE
            viewModel.clearAllUserAnswers(requireContext())
        }
        binding.pervBtn.setOnClickListener {
            if (!binding.pervBtn.isEnabled) return@setOnClickListener
            if (viewModel.quizId.value != 0) {
                viewModel.decreaseId()
            }
        }

        binding.nextBtn.setOnClickListener {
            if (!binding.nextBtn.isEnabled) return@setOnClickListener
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
            viewModel.quizList.value?.getOrNull(id)?.let { updateUi(it) }
        }
    }

    fun updateUi(item: Quiz) {
        binding.questionText.text = item.question

        val items = item.options.mapIndexed { index, opt ->
            var optionStatus = OptionStatus.INIT
            if (item.userAnswer != null) {
                optionStatus = when {
                    opt == item.answer -> OptionStatus.CORRECT
                    opt == item.userAnswer -> OptionStatus.WRONG
                    else -> OptionStatus.INIT
                }
            }
            Triple(index, opt, optionStatus)
        }

        binding.optionsListView.setup(
            items = items,
            onClick = { i ->
                if (item.userAnswer == null) {
                    setNavButtonsEnabled(false)
                    binding.optionsListView.setEnableClick(false)
                    viewModel.updateQuizField(requireContext(), i.second)
                    onItemClick(i.second)
                }
            }
        )

        binding.optionsListView.setEnableClick(item.userAnswer == null)

        binding.pervBtn.visibility = if (viewModel.quizId.value == 0) View.GONE else View.VISIBLE
        binding.nextBtn.visibility =
            if (viewModel.quizId.value == viewModel.quizList.value!!.size - 1) View.GONE else View.VISIBLE
    }


    private fun onItemClick(userAnswer: String) {
        val item = viewModel.quizList.value!![viewModel.quizId.value!!]
        binding.questionText.text = item.question
        val items =
            item.options.mapIndexed { index, opt ->
                var optionStatus = OptionStatus.INIT
                if (opt == item.answer) {
                    optionStatus = OptionStatus.CORRECT
                }
                if (opt == userAnswer && userAnswer != item.answer) {
                    optionStatus = OptionStatus.WRONG
                }

                Triple(index, opt, optionStatus)
            }
                .toList()
        binding.optionsListView.updateItems(items)

        showFeedback(userAnswer == item.answer)
        if (viewModel.quizId.value != viewModel.quizList.value!!.size - 1) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                viewModel.increaseId()
                setNavButtonsEnabled(true)
            }
        } else {
            setNavButtonsEnabled(true)
        }
    }


    private fun showFeedback(isCorrect: Boolean) {
        val message = if (isCorrect) "درسته" else "اشتباهه"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setNavButtonsEnabled(enabled: Boolean) {
        binding.pervBtn.isEnabled = enabled
        binding.nextBtn.isEnabled = enabled
    }

}
