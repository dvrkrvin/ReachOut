package com.lincolnstewart.android.reachout.ui.setup.tabs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.lincolnstewart.android.reachout.R

class AddContactFragment : Fragment() {

    companion object {
        fun newInstance() = AddContactFragment()
    }

    private lateinit var viewModel: AddContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // For conventional xml
        return inflater.inflate(R.layout.fragment_add_contact, container, false)

        // For composable
//        return ComposeView(requireContext()).apply {
//            setContent {
//                CreateContactForm()
//            }
//        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddContactViewModel::class.java)
        // TODO: Use the ViewModel
    }

//    @Composable
//    fun CreateContactForm() {
//
//        var firstName by remember { mutableStateOf("") }
//        var lastName by remember { mutableStateOf("") }
//        var phoneNumber by remember { mutableStateOf("") }
//
//        Column(Modifier.padding(16.dp)) {
//            OutlinedTextField(
//                value = firstName,
//                onValueChange = { firstName = it },
//                label = { Text("First Name") }
//            )
//            OutlinedTextField(
//                value = lastName,
//                onValueChange = { lastName = it },
//                label = { Text("Last Name") }
//            )
//            OutlinedTextField(
//                value = phoneNumber,
//                onValueChange = { phoneNumber = it },
//                label = { Text("Phone Number") }
//            )
//            Button(onClick = {
//                // Handle form submission
//            }) {
//                Text("Submit")
//            }
//        }
//    }

}