package com.lincolnstewart.android.reachout.ui.setup.tabs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lincolnstewart.android.reachout.R

const val TAG = "ChildOneFragment"

class ChildOneFragment : Fragment() {

    companion object {
        fun newInstance() = ChildOneFragment()
    }

    private lateinit var viewModel: ChildOneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_child_one, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChildOneViewModel::class.java)
        // TODO: Use the ViewModel
    }

}