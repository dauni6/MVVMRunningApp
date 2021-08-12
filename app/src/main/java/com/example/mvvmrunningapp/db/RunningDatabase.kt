package com.example.mvvmrunningapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RunConverters::class)
abstract class RunningDatabase : RoomDatabase() {
// dagger-hilt를 사용하면 hilt가 알아서 database를 singleton으로 만들어 주기때문에 thread safety를 걱정할 필요가 없다!!!

    abstract fun getRunDao(): RunDAO

}
