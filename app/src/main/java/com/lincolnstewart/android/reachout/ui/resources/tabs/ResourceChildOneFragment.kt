package com.lincolnstewart.android.reachout.ui.resources.tabs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lincolnstewart.android.reachout.R

const val TAG = "ResourceChildOneFragment"

class ResourceChildOneFragment : Fragment() {

    companion object {
        fun newInstance() = ResourceChildOneFragment()
    }

    private lateinit var viewModel: ResourceChildOneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "RCOF onCreateView called")
        return inflater.inflate(R.layout.fragment_resource_child_one, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResourceChildOneViewModel::class.java)
        // TODO: Use the ViewModel
    }

}