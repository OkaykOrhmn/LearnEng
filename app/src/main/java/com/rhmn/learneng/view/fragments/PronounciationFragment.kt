package com.rhmn.learneng.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rhmn.learneng.R
import com.rhmn.learneng.data.model.Pronunciation
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.initializeSTT(requireContext())
        } else {
            Toast.makeText(requireContext(), "Microphone permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPronounciationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dayId = PronounciationFragmentArgs.fromBundle(requireArguments()).dayId
        viewModel.fetchPronunciationList(requireContext(), dayId)

        setupObservers()
        setupPermissionAndSTT()
        setupClickListeners()
    }

    private fun updateUI(p: Pronunciation, id: Int) {
//        binding.pervBtn.visibility = if (id > 0) View.VISIBLE else View.GONE
//        binding.nextBtn.visibility =
//            if (id < (viewModel.pronunciationList.value?.size ?: 0) - 1) View.VISIBLE else View.GONE
        binding.inputField.text = ""
        binding.wordText.text = p.word.sentence
    }

    private fun setupObservers() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)

            // Disable UI controls during loading
            val enabled = !isLoading
            binding.micButton.isEnabled = enabled
//            binding.pervBtn.isEnabled = enabled
//            binding.nextBtn.isEnabled = enabled

            if (isLoading) {
                binding.wordText.text = "" // Clear word text while loading
                binding.inputField.text = ""
                binding.errorText.visibility = View.GONE
            }
        }

        viewModel.pronunciationId.observe(viewLifecycleOwner) { id ->
            val list = viewModel.pronunciationList.value
            if (list.isNullOrEmpty()) return@observe
            if (id !in list.indices) return@observe
            updateUI(list[id], id)
        }
        viewModel.pronunciationList.observe(viewLifecycleOwner) { list ->
            val id = viewModel.pronunciationId.value ?: 0
            if (list.isNullOrEmpty()) return@observe
            if (id !in list.indices) return@observe
            updateUI(list[id], id)
        }
        viewModel.recognizedText.observe(viewLifecycleOwner) { text ->
            if (text.startsWith("Error:")) {
                binding.errorText.visibility = View.VISIBLE
                return@observe
            }
            binding.inputField.text = text
            checkPronunciationMatch(text)
        }

        viewModel.isListening.observe(viewLifecycleOwner) { isListening ->
            binding.micButton.isEnabled = !isListening
            val colorRes = if (isListening) R.color.white else R.color.yellow
            val bgDrawableRes =
                if (isListening) R.drawable.circle_background_active else R.drawable.circle_background_deactive

            binding.micButton.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes))
            binding.micButton.background =
                ContextCompat.getDrawable(requireContext(), bgDrawableRes)
            binding.statusTv.text = if (isListening) "صحبت کنید" else "روی دکمه میکروفون بزنید"
        }
    }

    private fun checkPronunciationMatch(recognizedText: String) {
        val pronunciationId = viewModel.pronunciationId.value ?: return
        val list = viewModel.pronunciationList.value ?: return
        if (pronunciationId !in list.indices) return

        val expectedSentence = list[pronunciationId].word.sentence.lowercase(Locale.getDefault())
            .replace(Regex("[^a-z]"), "")
        val cleanedText = recognizedText.lowercase(Locale.getDefault()).replace(Regex("[^a-z]"), "")

//        binding.pervBtn.isEnabled = false
//        binding.nextBtn.isEnabled = false
        binding.micButton.isEnabled = false

        if (cleanedText == expectedSentence) {
            Toast.makeText(requireContext(), "درسته", Toast.LENGTH_SHORT).show()
            binding.errorText.visibility = View.GONE

            // Delay then advance to next
            viewLifecycleOwner.lifecycleScope.launch {
                kotlinx.coroutines.delay(1000)
                if (pronunciationId < list.lastIndex) viewModel.increaseId() else findNavController().popBackStack()
//                binding.pervBtn.isEnabled = true
//                binding.nextBtn.isEnabled = true
                binding.micButton.isEnabled = true
            }
        } else {
            Toast.makeText(requireContext(), "نادرسته", Toast.LENGTH_SHORT).show()
            binding.errorText.visibility = View.VISIBLE

//            binding.pervBtn.isEnabled = true
//            binding.nextBtn.isEnabled = true
            binding.micButton.isEnabled = true
        }
    }

    private fun setupPermissionAndSTT() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.initializeSTT(requireContext())
            }

            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(
                    requireContext(),
                    "Microphone permission is needed for speech recognition",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun setupClickListeners() {
        binding.micButton.setOnClickListener {
            binding.errorText.visibility = View.GONE
            binding.inputField.text = ""
            viewModel.startListening()
        }
//        binding.pervBtn.setOnClickListener { viewModel.decreaseId() }
//        binding.nextBtn.setOnClickListener { viewModel.increaseId() }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainContainer.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.destroySTT()
    }
}
