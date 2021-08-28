package com.example.mvvmrunningapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.databinding.ActivityMainBinding
import com.example.mvvmrunningapp.db.RunDAO
import com.example.mvvmrunningapp.extensions.toGone
import com.example.mvvmrunningapp.extensions.toVisible
import com.example.mvvmrunningapp.other.Constants
import com.example.mvvmrunningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val navigationController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainNavHostContainer) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) // 호스트인 액티비티에 actionbar가 없으면 각 프래그먼트에서 setHasOptionsMenu(true)로 해도 나타나지 않음.

        navigateToTrackingFragmentIfNeeded(intent)

        setUpNavigationWithActionBar()

    }

    // 액티비티가 destroy되지 않고 다시 열렸을 때 onCreate를 타지 않고 onNewIntent로 오게된다.
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.d("onNewIntent is called.")
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun setUpNavigationWithActionBar() = with(binding) {
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.setupFragment, R.id.runFragment, R.id.statisticsFragment, R.id.settingsFragment, R.id.trackingFragment)) // 탭 간 전환시 업버튼이 뜨지 않도록 하기 위함
        bottomNav.setupWithNavController(navigationController)
        toolbar.setupWithNavController(navigationController, appBarConfiguration)
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.runFragment, R.id.statisticsFragment, R.id.settingsFragment -> bottomNav.toVisible()
                else -> bottomNav.toGone()
            }
        }
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navigationController.navigate(R.id.action_global_trackingFragment)
        }
    }

    companion object {

        fun newIntent(context: Context) = Intent(
            context,
            MainActivity::class.java
        ).apply {
            action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        }

    }

}
