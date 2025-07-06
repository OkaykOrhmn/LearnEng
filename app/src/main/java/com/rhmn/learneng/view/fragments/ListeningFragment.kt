package com.rhmn.learneng.view.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rhmn.learneng.databinding.FragmentListeningBinding
import com.rhmn.learneng.viewmodel.ListeningViewModel

class ListeningFragment : Fragment() {

    companion object {
        fun newInstance() = ListeningFragment()
    }

    private var _binding: FragmentListeningBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListeningViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListeningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dayId = ListeningFragmentArgs.fromBundle(requireArguments()).dayId


        viewModel.fetchListeningList(requireContext(), dayId)

        viewModel.listeningItem.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            binding.title.text = it.title

        }

        viewModel.dialogItem.observe(viewLifecycleOwner) {
            var text = it.map { dialogue -> dialogue.text }.toList()
            binding.conversationTv.text = text.joinToString(separator = "")
        }


    }
}