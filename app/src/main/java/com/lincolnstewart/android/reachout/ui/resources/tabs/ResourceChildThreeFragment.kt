package com.lincolnstewart.android.reachout.ui.resources.tabs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lincolnstewart.android.reachout.R

class ResourceChildThreeFragment : Fragment() {

    companion object {
        fun newInstance() = ResourceChildThreeFragment()
    }

    private lateinit var viewModel: ResourceChildThreeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resource_child_three, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResourceChildThreeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}