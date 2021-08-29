package com.example.mvvmrunningapp.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmrunningapp.db.Run
import com.example.mvvmrunningapp.other.SortType
import com.example.mvvmrunningapp.repository.MainRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    private val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTime = mainRepository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE // default

    // 생성자와 함께 실행
    init {
        // runs field에 LiveData가 emit 될 때 마다 아래의 콜백이 호출된다.
        runs.addSource(runsSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                Timber.d("addSource - TYPE_DATE is called.")
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                Timber.d("addSource - TYPE_DISTANCE is called.")
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByCaloriesBurned) { result ->
            if (sortType == SortType.CALORIES_BURNED) {
                Timber.d("addSource - TYPE_CALORIES_BURNED is called.")
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByTime) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                Timber.d("addSource - TYPE_RUNNING_TIME is called.")
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                Timber.d("addSource - TYPE_AVG_SPEED is called.")
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType) {
        SortType.DATE -> runsSortedByDate.value?.let {
            Timber.d("sortRuns() - TYPE_DATE is called.")
            runs.value = it
        }
        SortType.DISTANCE -> runsSortedByDistance.value?.let {
            Timber.d("sortRuns() - TYPE_DISTANCE is called.")
            runs.value = it
        }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let {
            Timber.d("sortRuns() - TYPE_CALORIES_BURNED is called.")
            runs.value = it
        }
        SortType.RUNNING_TIME -> runsSortedByTime.value?.let {
            Timber.d("sortRuns() - TYPE_RUNNING_TIME is called.")
            runs.value = it
        }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let {
            Timber.d("sortRuns() - TYPE_AVG_SPEED is called.")
            runs.value = it
        }.also {
            this.sortType = sortType
        }
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

}
