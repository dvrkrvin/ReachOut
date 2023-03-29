package com.lincolnstewart.android.reachout.ui.resources.tabs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.ui.resources.ResourcesViewModel

const val TAG = "ResourceChildOneFragment"

// Helpful articles tab
class ResourceChildOneFragment : Fragment() {

    companion object {
        fun newInstance() = ResourceChildOneFragment()
    }

    private lateinit var sharedResourcesViewModel: ResourcesViewModel
    private lateinit var viewModel: ResourceChildOneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "RCOF onCreateView called")
        viewModel = ViewModelProvider(this).get(ResourceChildOneViewModel::class.java)
        sharedResourcesViewModel = ViewModelProvider(this).get(ResourcesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_resource_child_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.retrieveArticleLinks()
    }


}