package com.example.mvvmrunningapp.other

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mvvmrunningapp.databinding.MarkerViewBinding
import com.example.mvvmrunningapp.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    rootView : ViewGroup,
    context: Context,
    layoutId: Int
) : MarkerView(context, layoutId) {

    private val binding: MarkerViewBinding by lazy {
        MarkerViewBinding.inflate(LayoutInflater.from(context), rootView, false)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }

        val currentRunId = e.x.toInt()
        val run = runs[currentRunId]

        with (binding) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.KOREA)
            tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.avgSpeedInKMH}km/h"
            tvAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distanceInMeters / 1000f}km"
            tvDistance.text = distanceInKm

            tvDuration.text = TrackingUtil.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned = "${run.caloriesBurned}kcal"
            tvCaloriesBurned.text = caloriesBurned
        }

    }

}
