package com.example.mvvmrunningapp.other

import android.graphics.Color

object Constants {

    /** DB */
    const val RUNNING_DATABASE_NAME = "running_db"

    /** PERMISSION */
    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    /** SERVICE */
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    /** NOTIFICATION */
    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"
    const val NOTIFICATION_ID = 1

    /** LOCATION */
    const val LOCATION_UPDATE_INTERVAL = 5000L // 5초 마다 location을 업데이트
    const val FASTEST_LOCATION_INTERVAL = 2000L // 최소 2초 위치 간격(메모리 리소스를 아끼기 위함)

    /** POLYLINE */
    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    /** TIMER */
    const val TIMER_UPDATE_INTERVAL = 50L

    /** SharedPreferences */
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT= "KEY_WEIGHT"

}
