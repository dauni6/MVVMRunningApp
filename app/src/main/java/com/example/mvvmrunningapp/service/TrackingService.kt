package com.example.mvvmrunningapp.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.mvvmrunningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.mvvmrunningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.mvvmrunningapp.other.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

class TrackingService : LifecycleService() {

    // intent를 서비스에 보낼 때 callback됨.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Started or resumed service.")
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service.")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service.")
                }
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }


    companion object {

        fun newServiceIntent(context: Context, action: String) = Intent(context, TrackingService::class.java).also {
            it.action = action
            context.startService(it)
        }

    }

}
