package com.rhmn.learneng.view.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.data.model.Question
import com.rhmn.learneng.data.model.QuizType
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

    private var answer = ""
    private var answerId = 0

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

    private val answerAClick = View.OnClickListener {
        if (viewModel.selectedOption.value != null) {
            return@OnClickListener
        }
        if (binding.answerABtn.isEnabled) {
            viewModel.setSelectionOption(0)

        }
    }
    private val answerBClick = View.OnClickListener {
        if (viewModel.selectedOption.value != null) {
            return@OnClickListener
        }
        if (binding.answerBBtn.isEnabled) {
            viewModel.setSelectionOption(1)

        }
    }
    private val answerCClick = View.OnClickListener {
        if (viewModel.selectedOption.value != null) {
            return@OnClickListener
        }
        if (binding.answerCBtn.isEnabled) {
            viewModel.setSelectionOption(2)

        }
    }
    private val answerDClick = View.OnClickListener {
        if (viewModel.selectedOption.value != null) {
            return@OnClickListener
        }
        if (binding.answerDBtn.isEnabled) {
            viewModel.setSelectionOption(3)

        }
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
                binding.actionBar.setTitleText("آزمون خوانش")
            }

            QuizType.GRAMMAR -> {
                binding.actionBar.setTitleText("آزمون گرامر")
            }

            QuizType.FINAL -> {
                binding.actionBar.setTitleText("آزمون نهایی")
            }
        }

        viewModel.fetchReading(requireContext(), quizType)

        viewModel.readingItem.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.readingSection.visibility = View.GONE
                return@observe
            }
            binding.readingTitle.text = it.title
            binding.readingText.text = it.text
            binding.translateText.text = it.translation

        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (!it) {
                val numerics = viewModel.quizList.value!!.mapIndexed { index, question ->
                    val status =
                        if (question.userAnswer == null) false else if (question.userAnswer == question.answer) true else null
                    Triple(index, question.id == viewModel.quizId.value, status)
                }
                binding.numericListView.setup(
                    numerics,
                    onClick = { item ->
                        viewModel.setId(item.first)
                    },
                )
            }
        }

        binding.answerABtn.setOnClickListener(answerAClick)
        binding.answerBBtn.setOnClickListener(answerBClick)
        binding.answerCBtn.setOnClickListener(answerCClick)
        binding.answerDBtn.setOnClickListener(answerDClick)

        viewModel.quizList.observe(viewLifecycleOwner) {
            updateNumList(it)

            if (viewModel.quizList.value!!.all { it.userAnswer != null }) {
                var gList: List<Boolean>? = null
                var rList: List<Boolean>? = null
                var fList: List<Boolean>? = null
                var dayStep = DayStep.READ
                when (quizType) {
                    QuizType.READING -> {
                        rList = viewModel.quizList.value!!.map { it.userAnswer == it.answer }
//                        dayStep = DayStep.LISTEN
                        dayStep = DayStep.FNL_QUIZ

                    }

                    QuizType.GRAMMAR -> {
                        gList = viewModel.quizList.value!!.map { it.userAnswer == it.answer }
                        dayStep = DayStep.READ
                    }

                    QuizType.FINAL -> {
                        fList = viewModel.quizList.value!!.map { it.userAnswer == it.answer }
                        dayStep = DayStep.FINISH
                        if (dayStatusViewModel.isDayStatusExist(dayId + 1)) {
                            dayStatusViewModel.updateDayStatus(
                                requireContext(),
                                dayId + 1,
                                newDayStep = DayStep.VOCAL,
                                )
                        }

                    }
                }

                dayStatusViewModel.updateDayStatus(
                    requireContext(),
                    dayId,
                    newDayStep = dayStep,
                    newGrammarQuizResult = gList,
                    newReadingQuizResult = rList,
                    newFinalQuizResult = fList,
                )

                val action =
                    QuizFinishDialogDirections.actionToQuizFinishDialog(
                        quizType = quizType,
                        dayId = dayId
                    )

                findNavController().navigate(action)
            }
        }

        viewModel.selectedOption.observe(viewLifecycleOwner) { selected ->

            if (selected == null) {
                clearButtonColors()
                return@observe
            }

            val selectedText = checkAnswer(selected)
            showFeedback(selectedText == answer)
            viewModel.updateQuizField(selectedText)

            if (viewModel.quizId.value != viewModel.quizList.value!!.size - 1) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    viewModel.increaseId()
                }
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
                binding.translateBtn.text = "Hide Translate"
            } else {
                binding.translateText.visibility = View.GONE
                binding.translateBtn.text = "Show Translate"

            }
        }

        viewModel.quizId.observe(viewLifecycleOwner) { id ->
            val item = viewModel.quizList.value!![id]
            binding.questionText.text = item.question
            binding.answerABtn.text = item.options[0]
            binding.answerBBtn.text = item.options[1]
            binding.answerCBtn.text = item.options[2]
            binding.answerDBtn.text = item.options[3]
            answer = item.answer
            answerId = item.options.indexOf(item.options.find { it == answer })

            if (item.userAnswer != null) {
                binding.answerABtn.isEnabled = false
                binding.answerBBtn.isEnabled = false
                binding.answerCBtn.isEnabled = false
                binding.answerDBtn.isEnabled = false

                val selected = item.options.indexOf(item.options.find { it == item.userAnswer })
                checkAnswer(selected)
            } else {
                binding.answerABtn.isEnabled = true
                binding.answerBBtn.isEnabled = true
                binding.answerCBtn.isEnabled = true
                binding.answerDBtn.isEnabled = true
            }


            updateNumList(viewModel.quizList.value!!)
            binding.pervBtn.visibility =
                if (id == 0) View.GONE else View.VISIBLE
            binding.nextBtn.visibility =
                if (id == viewModel.quizList.value!!.size - 1) View.GONE else View.VISIBLE
        }
    }


    private fun clearButtonColors() {
        listOf(
            binding.answerABtn, binding.answerBBtn,
            binding.answerCBtn, binding.answerDBtn
        ).forEach {
            it.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun showFeedback(isCorrect: Boolean) {
        val message = if (isCorrect) "درسته" else "اشتباهه"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun updateNumList(list: List<Question>) {

        val numerics = list.mapIndexed { index, it ->
            val status =
                if (it.userAnswer == null) false else if (it.userAnswer == it.answer) true else null
            Triple(index, it.id == viewModel.quizId.value, status)
        }
        binding.numericListView.updateItems(numerics)
    }

    fun checkAnswer(selected: Int): String {
        val selectedText = viewModel.quizList.value!![viewModel.quizId.value!!].options[selected]

        val color = if (selectedText == answer) Color.GREEN else Color.RED

        when (selected) {
            0 -> binding.answerABtn.setBackgroundColor(color)
            1 -> binding.answerBBtn.setBackgroundColor(color)
            2 -> binding.answerCBtn.setBackgroundColor(color)
            3 -> binding.answerDBtn.setBackgroundColor(color)
        }
        when (selected) {
            0 -> binding.answerABtn.setBackgroundColor(color)
            1 -> binding.answerBBtn.setBackgroundColor(color)
            2 -> binding.answerCBtn.setBackgroundColor(color)
            3 -> binding.answerDBtn.setBackgroundColor(color)
        }


        when (answerId) {
            0 -> binding.answerABtn.setBackgroundColor(Color.GREEN)
            1 -> binding.answerBBtn.setBackgroundColor(Color.GREEN)
            2 -> binding.answerCBtn.setBackgroundColor(Color.GREEN)
            3 -> binding.answerDBtn.setBackgroundColor(Color.GREEN)
        }

        return selectedText
    }
}
