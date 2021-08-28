package com.example.mvvmrunningapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.databinding.FragmentRunBinding
import com.example.mvvmrunningapp.databinding.FragmentSettingsBinding
import com.example.mvvmrunningapp.databinding.FragmentSetupBinding
import com.example.mvvmrunningapp.databinding.FragmentStatisticsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSettingsBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
