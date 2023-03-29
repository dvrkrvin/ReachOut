package com.lincolnstewart.android.reachout.ui.resources

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.databinding.FragmentResourcesBinding
import com.lincolnstewart.android.reachout.ui.resources.tabs.ResourceChildOneFragment
import com.lincolnstewart.android.reachout.ui.resources.tabs.ResourceChildThreeFragment
import com.lincolnstewart.android.reachout.ui.resources.tabs.ResourceChildTwoFragment

const val TAG = "ResourcesFragment"

class ResourcesFragment : Fragment(R.layout.fragment_resources) {

    private var _binding: FragmentResourcesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ResourcesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ResourcesViewModel::class.java)
        _binding = FragmentResourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Resources fragment onViewCreated called")

        // Initialize the TabLayout and ViewPager2 views
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        // Create a NavController instance
        val navController = findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.resourceChildOneFragment -> viewPager.currentItem = 0
                R.id.resourceChildTwoFragment -> viewPager.currentItem = 1
                R.id.resourceChildThreeFragment -> viewPager.currentItem = 2
                else -> {
                    // hide the TabLayout if the current destination is not a child fragment
                    tabLayout.visibility = View.VISIBLE
                }
            }
            // show the TabLayout if the current destination is a child fragment
            if (destination.parent?.id == R.id.navigation_resources) {
                tabLayout.visibility = View.VISIBLE
            }
        }

        // Connect the ViewPager2 to the NavController using a TabLayoutMediator
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "First Fragment"
                1 -> "Second Fragment"
                2 -> "Third Fragment"
                else -> ""
            }
        }
        viewPager.adapter = ParentFragmentAdapter(this)
        tabLayoutMediator.attach()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResourcesViewModel::class.java)
        // TODO: Use the ViewModel
    }
}

class ParentFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3 // number of fragments to display

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ResourceChildOneFragment()
            1 -> ResourceChildTwoFragment()
            2 -> ResourceChildThreeFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}