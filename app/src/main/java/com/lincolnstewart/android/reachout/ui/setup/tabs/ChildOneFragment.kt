package com.lincolnstewart.android.reachout.ui.setup.tabs

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lincolnstewart.android.reachout.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.flow.collect
import java.util.UUID

private const val TAG = "ChildOneFragment"

class ChildOneFragment : Fragment() {

    //region Data members
    private var contactsReadBefore = false

    private var _binding: FragmentChildOneBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val childOneViewModel: ChildOneViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                readContacts()
            } else {
                Log.i("Permission: ", "Denied")
            }
        }
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

        navController = findNavController()
        navController.setGraph(R.navigation.mobile_navigation)

        binding.addContactFab.setOnClickListener {
            onAddContactFabClicked()
        }

        binding.removeContactFab.setOnClickListener {
            onRemoveContactFabClicked()
        }

        // If contacts have never been read; Request permission to read them, read them into Rooms database,
        // and set shared preferences value to reflect that contacts have now been read within the lifetime
        // of the applications install
        val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        contactsReadBefore = prefs.getBoolean("contacts_read_before", false)
        if (!contactsReadBefore) {
            requestPermission()
        }

        // Set contacts into recycler view
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
                // permission granted, read contacts
                readContacts()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) -> {
                // TODO?: Explain why we need the permission
            }
            else -> {
                // Permission not yet asked, request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun readContacts() {
        // permission granted, read contacts
        val importedContacts = childOneViewModel.readContacts(requireContext())
        Log.d(TAG, "Contacts received from phone: ${importedContacts.count()}")

        // Insert all imported contacts into the Rooms database
        childOneViewModel.addContacts(importedContacts)

        // Set in shared prefs that contacts have been read before
        val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        prefs.edit().putBoolean("contacts_read_before", true).apply()
    }

    private fun setRecyclerViewContent() {
        // Revert contact FAB back to Add if there are no selected contacts
        if (childOneViewModel.selectedContacts.all{ !it.value } && (binding.addContactFab.visibility != View.VISIBLE)) {
            childOneViewModel.selectedContacts.clear()
            fadeOutView(binding.removeContactFab)
            fadeInView(binding.addContactFab)
        }

        val composeView = requireView().findViewById<ComposeView>(R.id.compose_view)
        lifecycleScope.launch {
                childOneViewModel.loadContacts().collect { contacts ->
                    val alphabetizedContacts = contacts.sortedBy { it.displayName[0].uppercaseChar() }
                    composeView.setContent { RecyclerView(alphabetizedContacts, childOneViewModel.selectedContacts) }

                }
        }

    }
    
    private fun onAddContactFabClicked() {
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

//        Log.d(TAG, "Selected Contacts: $childOneViewModel.selectedContacts")

        lifecycleScope.launch{
            // Remove contacts
            val currentlySelectedContacts = childOneViewModel.selectedContacts.filter { it.value }.keys.toList()
            childOneViewModel.removeContacts(currentlySelectedContacts)

            childOneViewModel.selectedContacts.clear()
        }

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
                    .background(Color(0xFFFE4D4D)),
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
    // NOTE: The global selectedContacts variable is not used or referenced in this function
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun RecyclerView(contacts: List<Contact>, selectedContacts: MutableMap<UUID, Boolean>) {
        LazyColumn(modifier = Modifier.padding(vertical = 0.dp)) {
            val grouped = contacts.groupBy { it.displayName[0].uppercaseChar() }

            grouped.forEach { (initial, contacts) ->
                stickyHeader {
                    CharacterHeader(character = initial)
                }

                items(contacts) { contact ->
                    val isSelected = selectedContacts[contact.id] ?: false

                    ListItem(
                        circleText = contact.displayName.take(1).uppercase(),
                        text = contact.displayName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) Color.Gray else Color.White)
                            .pointerInput(Unit) {

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
                                            selectedContacts[contact.id] =
                                                !(selectedContacts[contact.id] ?: false)
                                            setRecyclerViewContent()

                                        }
                                    }
                                )
                            }
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }


    @Composable
    fun CharacterHeader(character: Char) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(vertical = 1.dp, horizontal = 16.dp)
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