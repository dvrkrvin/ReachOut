package com.lincolnstewart.android.reachout

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.lincolnstewart.android.reachout.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

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
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    Log.d(TAG, "HOME SELECTED")
                    true
                }
                R.id.navigation_setup -> {
                    navController.navigate(R.id.navigation_setup)
                    Log.d(TAG, "SETUP SELECTED")
                    true
                }
                R.id.navigation_resources -> {
                    navController.navigate(R.id.navigation_resources)
                    Log.d(TAG, "RESOURCES SELECTED")
                    true
                }
                R.id.navigation_help -> {
                    navController.navigate(R.id.navigation_help)
                    Log.d(TAG, "HELP SELECTED")
                    true
                }
                else -> false
            }
        }

        // Listen to navigation changes to check correct bottom nav item when navigating indirectly
        // (e.g. user pressing pressing the back button)
        val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
        }
        navController.addOnDestinationChangedListener(navListener)
    }

    private fun reachOut() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.menu.findItem(R.id.navigation_reach)?.isChecked = true

        val menuItem = bottomNavigationView.menu.findItem(R.id.navigation_reach)
        Log.d(TAG, "Reach Out MenuItem: $menuItem")

        navController.navigate(R.id.navigation_reach)
    }
}

