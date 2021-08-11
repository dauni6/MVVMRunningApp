package com.example.mvvmrunningapp.db

import android.graphics.Bitmap
import androidx.room.Entity

@Entity(tableName = "running_table")
data class Run(
    var img: Bitmap? = null,
    var time
)
