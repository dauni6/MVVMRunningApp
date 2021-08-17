package com.example.mvvmrunningapp.repository

import com.example.mvvmrunningapp.db.Run
import com.example.mvvmrunningapp.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDAO: RunDAO
)  {

    suspend fun insertRun(run: Run) = runDAO.insertRun(run)

    suspend fun deleteRun(run: Run) = runDAO.deleteRun(run)

    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortedByDate() // Livedata는 비동기이기때문에 suspend로 할 필요가 없다.

    fun getAllRunsSortedByDistance() = runDAO.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis() = runDAO.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed() = runDAO.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortedByCaloriesBurned()

    fun getTotalTimeInMills() = runDAO.getTotalTimeInMillis()

    fun getTotalDistance() = runDAO.getTotalDistance()

    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()

    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()

}
