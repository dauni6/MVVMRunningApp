package com.example.mvvmrunningapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mvvmrunningapp.R
import com.example.mvvmrunningapp.databinding.ActivityMainBinding
import com.example.mvvmrunningapp.db.RunDAO
import com.example.mvvmrunningapp.extensions.toGone
import com.example.mvvmrunningapp.extensions.toVisible
import dagger.hilt.android.AndroidEntryPoint
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

        setUpNavigationWithActionBar()

    }

    private fun setUpNavigationWithActionBar() = with(binding) {
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.runFragment, R.id.statisticsFragment, R.id.settingsFragment)) // 탭 간 전환시 업버튼이 뜨지 않도록 하기 위함
        bottomNav.setupWithNavController(navigationController)
        toolbar.setupWithNavController(navigationController, appBarConfiguration)
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.runFragment, R.id.statisticsFragment, R.id.settingsFragment -> bottomNav.toVisible()
                else -> bottomNav.toGone()
            }
        }
    }

}
