package com.example.mvvmrunningapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.databinding.FragmentRunBinding
import com.example.mvvmrunningapp.databinding.FragmentSetupBinding
import com.example.mvvmrunningapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment() {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRunBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() = with(binding) {
        fab.setOnClickListener {
            val action = RunFragmentDirections.actionRunToTracking()
            findNavController().navigate(action)
        }
    }

}
