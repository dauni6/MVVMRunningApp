package com.example.mvvmrunningapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.adapters.RunAdapter
import com.example.mvvmrunningapp.databinding.FragmentRunBinding
import com.example.mvvmrunningapp.databinding.FragmentSetupBinding
import com.example.mvvmrunningapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.mvvmrunningapp.other.SortType
import com.example.mvvmrunningapp.other.TrackingUtil
import com.example.mvvmrunningapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private lateinit var runAdapter: RunAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRunBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        initListeners()
        initRecyclerView()
        initObservers()
    }

    private fun initListeners() = with(binding) {
        fab.setOnClickListener {
            val action = RunFragmentDirections.actionRunToTracking()
            findNavController().navigate(action)
        }
    }

    private fun initRecyclerView() = binding.rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initObservers() {

        when (viewModel.sortType) {
            SortType.DATE -> binding.spFilter.setSelection(SortType.DATE.position)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(SortType.RUNNING_TIME.position)
            SortType.DISTANCE -> binding.spFilter.setSelection(SortType.DISTANCE.position)
            SortType.AVG_SPEED -> binding.spFilter.setSelection(SortType.AVG_SPEED.position)
            SortType.CALORIES_BURNED -> binding.spFilter.setSelection(SortType.CALORIES_BURNED.position)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    SortType.DATE.position -> viewModel.sortRuns(SortType.DATE)
                    SortType.RUNNING_TIME.position -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    SortType.DISTANCE.position -> viewModel.sortRuns(SortType.DISTANCE)
                    SortType.AVG_SPEED.position -> viewModel.sortRuns(SortType.AVG_SPEED)
                    SortType.CALORIES_BURNED.position -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun requestPermission() {
        if (TrackingUtil.hasLocationPermissions(requireContext())) {
            return
        }
        // todo ?????? ???????????? ????????? ????????? ??????????????? ??? ??????????????? ??????????????? ??????
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()}
        else{}
            requestPermission()
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        /** ???????????? ?????? requestPermission()??? ????????? ??????????????? hasLocationPermissions()?????? true?????? return?????? ?????? */
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}
