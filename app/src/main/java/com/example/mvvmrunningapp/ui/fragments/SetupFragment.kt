package com.example.mvvmrunningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.databinding.FragmentSetupBinding
import com.example.mvvmrunningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.mvvmrunningapp.other.Constants.KEY_NAME
import com.example.mvvmrunningapp.other.Constants.KEY_WEIGHT
import com.example.mvvmrunningapp.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSetupBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkFirstAppOpen(savedInstanceState)
        initListeners()

    }

    private fun checkFirstAppOpen(savedInstanceState: Bundle?) {
        if (!isFirstAppOpen) {
            // 바로 runFragment로 보내기
            // 그런데 사용자가 runFragment로 갔다가 백버튼을 누르면 setupFragment가 백스택에 들어가 있기 때문에 다시 setupFragment가 보이게 된다.
            // 이걸 방지하기 위해 setupFragment를 백스택에서 제거하자
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            val action = SetupFragmentDirections.actionSetupToRun()
            findNavController().navigate(
                action.actionId,
                savedInstanceState,
                navOptions)
        }
    }

    private fun initListeners() = with(binding) {
        tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                val action = SetupFragmentDirections.actionSetupToRun()
                findNavController().navigate(action)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }

        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply() // apply : asynchronous / commit : synchronous

        // change the toolbarTitle of MainActivity's toolbar
        val toolbarText = "Let's go, $name!"
        (requireActivity() as MainActivity).changeToolbarName(toolbarText)

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
