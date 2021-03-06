package com.example.mvvmrunningapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.mvvmrunningapp.db.RunningDatabase
import com.example.mvvmrunningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.mvvmrunningapp.other.Constants.KEY_NAME
import com.example.mvvmrunningapp.other.Constants.KEY_WEIGHT
import com.example.mvvmrunningapp.other.Constants.RUNNING_DATABASE_NAME
import com.example.mvvmrunningapp.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton // @Provides annotation만 사용하면 아래 메서드를 호출하는 곳에서 계속 새로운 인스턴스가 생긴다. 앱 생명주기 동안 동일한 인스턴스를 제공하려면 @Singleton annotation을 꼭 붙여줘야 한다
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext app: Context
    ) = app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) = sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

}
