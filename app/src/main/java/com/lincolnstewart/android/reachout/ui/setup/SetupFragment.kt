package com.lincolnstewart.android.reachout.ui.setup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.ui.setup.tabs.ChildOneFragment
import com.lincolnstewart.android.reachout.ui.setup.tabs.ChildTwoFragment

class SetupFragment : Fragment(R.layout.fragment_setup) {

    companion object {
        fun newInstance() = SetupFragment()
    }

    private lateinit var viewModel: SetupViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the TabLayout and ViewPager2 views
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        // Create a NavController instance
        val navController = findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.childOneFragment -> viewPager.currentItem = 0
                R.id.childTwoFragment -> viewPager.currentItem = 1
                else -> {
                    // hide the TabLayout if the current destination is not a child fragment
                    tabLayout.visibility = View.VISIBLE
                }
            }
            // show the TabLayout if the current destination is a child fragment
            if (destination.parent?.id == R.id.navigation_setup) {
                tabLayout.visibility = View.VISIBLE
            }
        }

        // Connect the ViewPager2 to the NavController using a TabLayoutMediator
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Contacts"
                1 -> "Notifications"
                else -> ""
            }
        }
        viewPager.adapter = ParentFragmentAdapter(this)
        tabLayoutMediator.attach()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SetupViewModel::class.java)
    }

}

class ParentFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2 // number of fragments to display

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChildOneFragment()
            1 -> ChildTwoFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
