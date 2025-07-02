package com.rhmn.learneng.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.data.model.Vocal
import com.rhmn.learneng.databinding.FragmentPronounciationBinding
import com.rhmn.learneng.viewmodel.DayViewModel
import com.rhmn.learneng.viewmodel.PronounciationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PronounciationFragment : Fragment() {

    private var _binding: FragmentPronounciationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PronounciationViewModel by viewModels()
    private val dayStatusViewModel: DayViewModel by activityViewModels()

    private var isListInitialized = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.initializeSTT(requireContext())
        } else {
            Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPronounciationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dayId = parentFragment?.arguments?.let { args ->
            DayFragmentArgs.fromBundle(args).dayId
        } ?: 0
        viewModel.dayId = dayId

        dayStatusViewModel.initialize(requireContext())

        viewModel.fetchPronunciationList(requireContext())
        viewModel.setId(0)

        viewModel.pronunciationId.observe(viewLifecycleOwner) { pronunciationId ->
            if (pronunciationId == null) return@observe

            updateNumList(viewModel.pronunciationList.value!!)

            binding.pervBtn.visibility = if (pronunciationId > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.nextBtn.visibility =
                if (pronunciationId < viewModel.pronunciationList.value!!.size - 1) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            val p = viewModel.pronunciationList.value?.get(pronunciationId)!!
            val success =
                (viewModel.pronunciationList.value?.find { it.id == p.id })?.success ?: false
            binding.inputField.text = if (success) p.word else ""
            binding.wordText.text = p.word

        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if(!it){
                val numerics = viewModel.pronunciationList.value!!.mapIndexed() {  index, pronunciation ->
                    Triple(index,pronunciation.id == viewModel.pronunciationId.value, pronunciation.success)
                }
                binding.numericListView.setup(
                    numerics,
                    onClick = { item ->
                        viewModel.setId(item.first)
                    },
                )
            }
        }

        viewModel.pronunciationList.observe(viewLifecycleOwner) { list ->
           updateNumList(list)
            if (list.all { it.success }) {
                dayStatusViewModel.updateDayStatus(
                    requireContext(),
                    dayId,
                    newDayStep = DayStep.DICT
                )
            }
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED -> {
                viewModel.initializeSTT(requireContext())
            }

            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(
                    context,
                    "Microphone permission is needed for speech recognition",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        binding.micButton.setOnClickListener {
            binding.inputField.text = ""
            viewModel.startListening()
        }

        viewModel.recognizedText.observe(viewLifecycleOwner) { text ->
            binding.inputField.text = text
            binding.pervBtn.isEnabled = false
            binding.nextBtn.isEnabled = false
            binding.micButton.isEnabled = false
            val cleanedText = text.lowercase(Locale.getDefault()).replace(Regex("[^a-z]"), "")
            val cleanedSentenceText =
                (viewModel.pronunciationList.value?.get(viewModel.pronunciationId.value!!)?.word
                    ?: "").lowercase(Locale.getDefault()).replace(Regex("[^a-z]"), "")

            if (cleanedText == cleanedSentenceText
            ) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "درسته", Toast.LENGTH_SHORT).show()
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000)
                    }
                    viewModel.updatePronunciationField(true)
                    val check =
                        viewModel.pronunciationId.value!! < viewModel.pronunciationList.value!!.size - 1
                    if (check) {
                        viewModel.increaseId()
                    }
                }
            } else {
                Toast.makeText(context, "نادرسته", Toast.LENGTH_SHORT).show()
            }

            binding.pervBtn.isEnabled = true
            binding.nextBtn.isEnabled = true
            binding.micButton.isEnabled = true
        }

        viewModel.isListening.observe(viewLifecycleOwner) { isListening ->
            binding.micButton.isEnabled = !isListening
            if (isListening) {
                binding.micButton.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.circle_background_red)
                binding.statusTv.text = "صحبت کنید"
            } else {
                binding.micButton.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.circle_background_blue)
                binding.statusTv.text = "روی دکمه میکروفون بزنید"
            }
        }

        binding.pervBtn.setOnClickListener {
            viewModel.decreaseId()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.increaseId()
        }

    }

    fun updateNumList(list: List<Vocal>){
        val numerics = list.mapIndexed() {  index, it ->
            Triple(index,it.id == viewModel.pronunciationId.value, it.success)
        }
        binding.numericListView.updateItems(numerics)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isListInitialized = false
    }

}