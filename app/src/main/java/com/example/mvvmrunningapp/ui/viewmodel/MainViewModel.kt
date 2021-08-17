package com.example.mvvmrunningapp.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.mvvmrunningapp.repository.MainRepository
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel() {

}
