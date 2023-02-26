package com.lincolnstewart.android.reachout.ui.setup.tabs

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lincolnstewart.android.reachout.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.lincolnstewart.android.reachout.ContactRepository
import com.lincolnstewart.android.reachout.database.ContactDao
import com.lincolnstewart.android.reachout.databinding.FragmentChildOneBinding
import com.lincolnstewart.android.reachout.model.Contact
import kotlinx.coroutines.*
import java.util.UUID

//TODO: I am now able to remove the list of selected contacts but the view doesn't
// always update immediately, suspected race conditions. Import contacts also doesn't update
// immediately anymore.
// TODO: Move the selectedContacts val to the viewModel if possible
// TODO: Import contacts only once
// TODO: Save imported contacts to room database

private const val TAG = "ChildOneFragment"

class ChildOneFragment : Fragment() {

    //region Data members
    private var _binding: FragmentChildOneBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val childOneViewModel: ChildOneViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ","Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    private val selectedContacts = mutableMapOf<UUID, Boolean>()
    //endregion

    // region Lifecycle Functions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val navHostFragment = childFragmentManager.findFragmentById(R.id.navigation_setup) as NavHostFragment
        navController = findNavController()
        navController.setGraph(R.navigation.mobile_navigation)

        binding.addContactFab.setOnClickListener {
            onAddContactFabClicked()
        }

        binding.removeContactFab.setOnClickListener {
            onRemoveContactFabClicked()
        }
        // Request permission to read contacts
        requestPermission()
        // Set contacts? into recycler view
        setRecyclerViewContent()
    }

    override fun onResume() {
        super.onResume()
//        Log.d(TAG, childOneViewModel.testContacts.toString())
        setRecyclerViewContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // endregion

    //region UI Related Functions
    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // permission granted
                val contactsList = childOneViewModel.readContacts(requireContext())
                Log.d(TAG, "Contacts received from phone: ${contactsList.count()}")
                childOneViewModel.importedContacts = contactsList
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) -> {
                // explain why we need the permission
            }
            else -> {
                // Permission not yet asked, request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    // If we have been given access and received contacts, set them in the RecyclerView,
    // otherwise display our test contacts
    private fun setRecyclerViewContent() {
        val composeView = requireView().findViewById<ComposeView>(R.id.compose_view)
        if (childOneViewModel.importedContacts.isNotEmpty()) {
            composeView.setContent { RecyclerView(childOneViewModel.importedContacts, selectedContacts) }
        } else {
            // Load the contacts from the Room ContactDatabase
            lifecycleScope.launch {
                val contacts = withContext(Dispatchers.IO) {
                    childOneViewModel.loadContacts()
                }
                composeView.setContent { RecyclerView(contacts, selectedContacts) }
                Log.d(TAG, contacts.toString())
            }
        }
    }
    
    private fun onAddContactFabClicked() {
        Log.d(TAG, "Add contact fab clicked")

        val navController = findNavController()
        // Fading animation for now, may replace with explosion later.
        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()
        navController.navigate(R.id.navigation_add_contact, null, options)
    }

    private fun onRemoveContactFabClicked() {
        Log.d(TAG, "Remove contact fab clicked")
//        Log.d(TAG, "Selected Contacts: $selectedContacts")
        // Remove contacts
        childOneViewModel.removeContacts(selectedContacts.keys.toList())

        // Update view, this doesn't appear to be working due to race condition
//        setRecyclerViewContent()

        // Toggle FAB
        fadeOutView(binding.removeContactFab)
        fadeInView(binding.addContactFab)
    }

    private fun fadeOutView(view: View) {
        view.apply {
            animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    visibility = View.GONE
                }
                .start()
        }
    }

    private fun fadeInView(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }
    }
    //endregion

    //region Composable Functions

    // Contact
    @Composable
    fun ListItem(
        circleText: String,
        text: String,
        modifier: Modifier = Modifier,
        imageSize: Dp = 48.dp,
        textPadding: Dp = 64.dp
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = circleText,
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = textPadding)
            )
        }
    }

    // Contacts list
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun RecyclerView(contacts: List<Contact>, selectedContacts: MutableMap<UUID, Boolean>) {
        LazyColumn(modifier = Modifier.padding(vertical = 0.dp)) {
            // TODO: Move this to view model
            val grouped = contacts.groupBy { it.displayName[0] }

            grouped.forEach { (initial, contacts) ->
                stickyHeader {
                    CharacterHeader(character = initial)
                }

                items(contacts) { contact ->
                    val isSelected = selectedContacts[contact.id] ?: false

                    ListItem(
                        circleText = contact.displayName.take(1),
                        text = contact.displayName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) Color.Gray else Color.White)
                            .pointerInput(Unit) {

                                // Hide add button, show delete button
                                detectTapGestures(
                                    onLongPress = {
                                        // If the user has contacts selected
                                        selectedContacts[contact.id] = true
                                        setRecyclerViewContent()

                                        // Swap add button with remove button
                                        fadeOutView(binding.addContactFab)
                                        if (binding.removeContactFab.visibility != View.VISIBLE) {
                                            fadeInView(binding.removeContactFab)
                                        }
                                    },
                                    onTap = {
                                        if (selectedContacts.isNotEmpty()) {
                                            selectedContacts[contact.id] = !(selectedContacts[contact.id] ?: false)
                                            Log.d(TAG, selectedContacts.toString())
                                            setRecyclerViewContent()

                                        }
                                    }
                                )
                            }
                    )
                }
            }
        }
    }


    @Composable
    fun CharacterHeader(character: Char) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(vertical = 2.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = character.toString(),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
//        RecyclerView(childOneViewModel.testContacts)
    }
    //endregion
}

// List item from tutorial, holding onto for reference
//    @Composable
//    fun ListItem(name: String) {
//        Surface(color = MaterialTheme.colors.primary,
//        modifier = Modifier.padding(vertical = 2.dp, horizontal = 0.dp)) {
//            Column(modifier = Modifier
//                .padding(20.dp)
//                .fillMaxWidth()) {
//                Row {
//                    Column(
//                        modifier = Modifier
//                            .weight(1f)
//                    ) {
//                        Text(text = "Course")
//                        Text(text = name, style = MaterialTheme.typography.h4.copy(
//                            fontWeight = FontWeight.ExtraBold
//                        ))
//                    }
//                    OutlinedButton(onClick = { showToast(name)}) {
//                        Text(text = "Show more")
//                    }
//                }
//            }
//        }
//    }

// Holding onto for testing purposes
//private fun showToast(text: String) {
//    Toast.makeText(binding.root.context, text, Toast.LENGTH_SHORT).show()
//}