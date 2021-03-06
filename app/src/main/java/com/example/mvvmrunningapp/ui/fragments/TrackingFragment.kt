package com.example.mvvmrunningapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.databinding.FragmentTrackingBinding
import com.example.mvvmrunningapp.db.Run
import com.example.mvvmrunningapp.extensions.toGone
import com.example.mvvmrunningapp.extensions.toVisible
import com.example.mvvmrunningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.mvvmrunningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.mvvmrunningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.mvvmrunningapp.other.Constants.MAP_ZOOM
import com.example.mvvmrunningapp.other.Constants.POLYLINE_COLOR
import com.example.mvvmrunningapp.other.Constants.POLYLINE_WIDTH
import com.example.mvvmrunningapp.other.TrackingUtil
import com.example.mvvmrunningapp.service.Polyline
import com.example.mvvmrunningapp.service.TrackingService
import com.example.mvvmrunningapp.ui.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    /** binding */
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    /** viewModel */
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var currentTimeInMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true) // Activity????????? ??????????????? true????????? Fragment????????? false??????. ?????? setHasOptionsMenu(true)????????? onCreateOptionsMenu()??? ????????? ??? ??????.
        return FragmentTrackingBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkStillSameCancelDialog(savedInstanceState) // ???????????? ??????
        initViews(savedInstanceState)
        initObservers()
    }

    private fun checkStillSameCancelDialog(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CancelTrackingDialog::javaClass.name
            ) as CancelTrackingDialog?
            cancelTrackingDialog?.setPositiveListener {
                stopRun()
            }
        }
    }

    private fun initViews(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }

        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDBWithScreenShot()
        }

    }

    private fun initObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it
            val formattedTime = TrackingUtil.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.toVisible()
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInMillis > 0L) { // ??????????????????
            this.menu?.getItem(0)?.toVisible()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog() {
        CancelTrackingDialog().apply {
            setPositiveListener {
                stopRun()
            }
        }.show(parentFragmentManager, CancelTrackingDialog::javaClass.name)
    }

    private fun stopRun() {
        binding.tvTimer.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        val action = TrackingFragmentDirections.actionTrackingToRun()
        findNavController().navigate(action)
    }

    // UI??????????????? isTracking?????? ???????????? ??????????????????
    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && currentTimeInMillis > 0L) {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.toVisible()
        } else if (isTracking) {
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.toGone()
            menu?.getItem(0)?.toVisible()
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDBWithScreenShot() {
        map?.snapshot { bitmap ->
            var distanceInMeter = 0
            pathPoints.forEach {
                distanceInMeter += TrackingUtil.calculatePolylineLength(it).toInt()
            }
            // (distanceInMeter / 1000f) => km??? ???????????? ??????
            // currentTimeInMillis / 1000f => second ?????????
            // currentTimeInMillis / 1000f / 60f => minute ?????????
            // currentTimeInMillis / 1000f / 60f / 60f => hour ?????????
            // round??? ???????????? ????????? avgSpeed??? single decimal??? ???????????? ??????
            // (distanceInMeter / 1000f) / (currentTimeInMillis / 1000f / 60f / 60f) => ??????????????? avgSpeed
            // round((distanceInMeter / 1000f) / (currentTimeInMillis / 1000f / 60f / 60f) * 10) => 10??? ?????? ??? round?????? ????????? decimal??? ???????????? ???.
            // round((distanceInMeter / 1000f) / (currentTimeInMillis / 1000f / 60f / 60f) * 10) / 10f => ??????????????? ?????? 10f?????? ????????? single decimal??? ?????? ??? ??????
            val avgSpeed =
                round((distanceInMeter / 1000f) / (currentTimeInMillis / 1000f / 60f / 60f) * 10) / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeter / 1000f) * weight).toInt()
            val run = Run(
                bitmap,
                dateTimestamp,
                avgSpeed,
                distanceInMeter,
                currentTimeInMillis,
                caloriesBurned
            )
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.mainRootView), // endRunAndSaveToDBWithScreenShot()??? ???????????? ??? ??? navigate()??? ?????? runFragment??? ???????????????, TrackingFragment??? View??? ???????????? SnackBar??? ?????? ???????????? ?????? TrackingFragment??? ???????????? ????????? ???????????? ????????????. ????????? TrackingFragment??? view??? ???????????? ?????????.
                getString(R.string.txt_save_run_successfully),
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    // ????????? ???????????? ??? ????????? ?????? ?????? polyline??? ????????????
    private fun addAllPolylines() {
        pathPoints.forEach {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(it)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        TrackingService.newServiceIntentWithStartService(requireContext(), action)

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        map = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

}
