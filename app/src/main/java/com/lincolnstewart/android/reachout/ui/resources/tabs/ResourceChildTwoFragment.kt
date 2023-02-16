package com.lincolnstewart.android.reachout.ui.resources.tabs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lincolnstewart.android.reachout.R

class ResourceChildTwoFragment : Fragment() {

    companion object {
        fun newInstance() = ResourceChildTwoFragment()
    }

    private lateinit var viewModel: ResourceChildTwoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resource_child_two, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResourceChildTwoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}