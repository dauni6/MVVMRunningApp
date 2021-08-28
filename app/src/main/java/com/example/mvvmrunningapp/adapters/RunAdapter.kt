package com.example.mvvmrunningapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmrunningapp.databinding.ItemRunBinding
import com.example.mvvmrunningapp.db.Run
import com.example.mvvmrunningapp.other.TrackingUtil
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RunViewHolder.from(parent)

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    class RunViewHolder(private val binding: ItemRunBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(run: Run) {

            with(binding) {
                Glide.with(binding.root)
                    .load(run.img)
                    .into(ivRunImage)

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = run.timestamp
                }
                val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.KOREA)
                tvDate.text = dateFormat.format(calendar.time)

                val avgSpeed = "${run.avgSpeedInKMH}km/h"
                tvAvgSpeed.text = avgSpeed

                val distanceInKm = "${run.distanceInMeters / 1000f}km"
                tvDistance.text = distanceInKm

                tvTime.text = TrackingUtil.getFormattedStopWatchTime(run.timeInMillis)

                val caloriesBurned = "${run.caloriesBurned}kcal"
                tvCalories.text = caloriesBurned
            }


        }


        companion object {
            fun from(parent: ViewGroup): RunViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemRunBinding.inflate(inflater, parent, false)
                return RunViewHolder(binding)
            }
        }

    }

    val diffCallback = object : DiffUtil.ItemCallback<Run>() {

        // 고유값 비교
        override fun areItemsTheSame(oldItem: Run, newItem: Run) = oldItem.id == newItem.id

        // 아이템 자체를 비교
        override fun areContentsTheSame(oldItem: Run, newItem: Run) = oldItem.hashCode() == newItem.hashCode()

    }

    // baackground에서 비동기로 동작
    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

}
