package com.example.mvvmrunningapp.ui.fragments

import android.Manifest
import android.os.Build
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
import com.example.mvvmrunningapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
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
    }

    private fun initListeners() = with(binding) {
        fab.setOnClickListener {
            val action = RunFragmentDirections.actionRunToTracking()
            findNavController().navigate(action)
        }
    }

    private fun requestPermission() {
        if (TrackingUtil.hasLocationPermissions(requireContext())) {
            return
        }
        // todo 아래 코드에서 퍼미션 부분이 중복되는게 좀 보기싫어서 추상화하고 싶다
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
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
            AppSettingsDialog.Builder(this).build().show()
        else
            requestPermission()
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        /** 사용하지 않음 requestPermission()의 첫번째 코드라인인 hasLocationPermissions()에서 true이면 return되기 때문 */
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
