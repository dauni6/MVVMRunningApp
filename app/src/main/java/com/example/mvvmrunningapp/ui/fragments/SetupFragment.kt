package com.example.mvvmrunningapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSetupBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

    }

    private fun initListeners() = with(binding) {
        tvContinue.setOnClickListener {
            val action = SetupFragmentDirections.actionSetupToRun()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
