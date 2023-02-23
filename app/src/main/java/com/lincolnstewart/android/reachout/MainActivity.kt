package com.lincolnstewart.android.reachout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lincolnstewart.android.reachout.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // Wire up ReachOut button
        binding.fab.setOnClickListener {
            reachOut()
        }

        navController.setGraph(R.navigation.mobile_navigation)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.miHome -> {
                    navController.navigate(R.id.navigation_home)
                    Log.d(TAG, "HOME SELECTED")
                    true
                }
                R.id.miSetup -> {
                    navController.navigate(R.id.navigation_setup)
                    Log.d(TAG, "SETUP SELECTED")
                    true
                }
                R.id.miResources -> {
                    navController.navigate(R.id.navigation_resources)
                    Log.d(TAG, "RESOURCES SELECTED")
                    true
                }
                R.id.miHelp -> {
                    navController.navigate(R.id.navigation_help)
                    Log.d(TAG, "HELP SELECTED")
                    true
                }
                else -> false
            }
        }

        val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
        }
        navController.addOnDestinationChangedListener(navListener)
    }

    private fun reachOut() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNavigationView.clearCheck()
        bottomNavigationView.menu.findItem(R.id.placeholder)?.isChecked = true
        navController.navigate(R.id.navigation_reach)
    }
}

