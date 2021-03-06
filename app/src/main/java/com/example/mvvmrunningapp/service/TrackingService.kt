package com.example.mvvmrunningapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.mvvmrunningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.mvvmrunningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.mvvmrunningapp.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.mvvmrunningapp.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.mvvmrunningapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.mvvmrunningapp.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.mvvmrunningapp.other.Constants.NOTIFICATION_ID
import com.example.mvvmrunningapp.other.Constants.TIMER_UPDATE_INTERVAL
import com.example.mvvmrunningapp.other.TrackingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var currentNotificationBuilder: NotificationCompat.Builder

    private val timeRunInSeconds = MutableLiveData<Long>()

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L // 언제 타이머를 시작했는지에 대한 초
    private var lastSecondTimestamp = 0L

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        initObservers()
    }

    private fun initObservers() {
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true // service가 죽었기 때문에 다시 시작할 수 있도록
        pauseTracking()
        postInitialValues()
        stopForeground(true) // foreground service에 대한 notification 지우기
        stopSelf() // stop whole service
    }

    // intent를 서비스에 보낼 때 callback됨. 최초 한 번만 실행??
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service...")
                       startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service.")
                    pauseTracking()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service.")
                    killService()
                }
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer {
            if (!serviceKilled) { // if문 안에 아래의 코드를 넣는 이유는, service와 notification이 종료되어도 observe()가 호출할 수 있기 때문에
                val notification = currentNotificationBuilder
                    .setContentText(TrackingUtil.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW // todo :: IMPORTANCE_LOW 제대로 알아보기
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun postInitialValues() {
        isTracking.postValue(false) // true가 되면 location을 tracking한다. false면 tracking을 중지한다. 중지되면 더 이상 업데이트를 할 필요가 없다
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = newServiceIntent(this, ACTION_PAUSE_SERVICE)
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = newServiceIntent(this, ACTION_START_OR_RESUME_SERVICE)
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if (!serviceKilled) {
            currentNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
        }

    }

    @SuppressLint("MissingPermission") // hasLocationPermissions()을 통해 권한을 확인받으므로 해당 Lint를 suppress한다
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtil.hasLocationPermissions(this)) { // todo :: 그럼 업데이트될 때마다 퍼미션이 있는지 체크를 계속 하게되어 리소스가 많이들지 않을까?
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        // Ctrl + O
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    locations.forEach {
                        addPathPoint(it)
                        Timber.d("NEW LOCATION : ${it.latitude}, ${it.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    private fun pauseTracking() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    companion object {

        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val timeRunInMillis = MutableLiveData<Long>()

        fun newServiceIntentWithStartService(context: Context, action: String) = Intent(context, TrackingService::class.java).also {
            it.action = action
            context.startService(it)
        }

        fun newServiceIntent(context: Context, action: String) = Intent(context, TrackingService::class.java).also {
            it.action = action
        }

    }

}

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>
