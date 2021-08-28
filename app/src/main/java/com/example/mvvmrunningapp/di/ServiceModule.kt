package com.example.mvvmrunningapp.di

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.mvvmrunningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped // @Singleton과 비슷한데 service에서는 @ServiceScoped를 통해서 singleton을 표현함
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app,
        0, // requestCode가 따로 필요하진 않아서 0으로 초기화
        MainActivity.newIntent(app),
        PendingIntent.FLAG_UPDATE_CURRENT // todo :: 무엇을 의미하는지 제대로 확인해보기
    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true) // Notification can be swiped away
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle(app.getString(R.string.txt_running_app))
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

}
