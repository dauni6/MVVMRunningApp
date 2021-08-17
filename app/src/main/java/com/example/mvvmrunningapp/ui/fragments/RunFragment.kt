package com.example.mvvmrunningapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    private val viewModel: MainViewModel by viewModels()

}
