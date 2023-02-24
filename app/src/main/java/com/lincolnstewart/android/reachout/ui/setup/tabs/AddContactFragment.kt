package com.lincolnstewart.android.reachout.ui.setup.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.databinding.FragmentAddContactBinding
import com.lincolnstewart.android.reachout.model.Contact
import java.util.*

private const val TAG = "AddContactFragment"

class AddContactFragment : Fragment() {

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AddContactFragment()
    }

    private lateinit var viewModel: ChildOneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // For conventional xml
//        return inflater.inflate(R.layout.fragment_add_contact, container, false)
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root

        // For composable
//        return ComposeView(requireContext()).apply {
//            setContent {
//                CreateContactForm()
//            }
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveContactButton.setOnClickListener { saveContactOnClicked() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChildOneViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun saveContactOnClicked() {
        Log.d(TAG, "Save contact clicked")

        // Create mew Contact
        val uuid = UUID.randomUUID()
        val name = binding.editTextContactName.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        val newContact = Contact(uuid, name, phoneNumber)

        Log.d(TAG, "New contact details: $name, $phoneNumber")


        // Add the contact
        viewModel.addContact(newContact)

        // Confirmation toast or similar
        Toast.makeText(binding.root.context, "Contact Added", Toast.LENGTH_SHORT).show()

        // Dismiss this fragment
        findNavController().popBackStack()
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