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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lincolnstewart.android.reachout.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lincolnstewart.android.reachout.databinding.FragmentChildOneBinding
import com.lincolnstewart.android.reachout.model.Contact

const val TAG = "ChildOneFragment"

//TODO: Fix application crashing if Contact has an empty data member
var importedContacts = mutableListOf<Contact>()

val testContacts = mutableListOf(
    Contact("Alice Dave", "123-456-7890", ""),
    Contact("Barrett Johann", "123-456-1234", ""),
    Contact("Charlie Brown",  "345-678-9012", ""),
    Contact("Dave Fill",  "456-789-0123", ""),
    Contact("Eve Sert",  "567-890-1234", ""),
    Contact("Frank Gora",  "678-901-2345", ""),
    Contact("Grace Tree",  "789-012-3456", ""),
    Contact("Julia Bell", "012-345-6789", ""),
    Contact("Julia Bell", "012-345-6789", ""),
    Contact("Barrett Flip", "123-456-1234", ""),
    Contact("Charlie Joe",  "345-678-9012", ""),
    Contact("Dave Davidson",  "456-789-0123", ""),
    Contact("Dave Moore", "456-789-0123", ""),
    Contact("Eve Tea",  "567-890-1234", ""),
    Contact("Frank Strog",  "678-901-2345", "")
)

class ChildOneFragment : Fragment() {

    private var _binding: FragmentChildOneBinding? = null
    private val binding get() = _binding!!

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
        // Request permission to read contacts
        requestPermission()
        // Set contacts? into recycler view
        setRecyclerViewContent()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // permission granted
                val contactsList = readContacts(requireContext())
                Log.d(TAG, "Contacts received: ${contactsList.count()}")
                importedContacts = contactsList
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

    //TODO: Move this function to the viewModel
    private fun readContacts(context: Context): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contentResolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )
        val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        val selection = "${ContactsContract.Contacts.HAS_PHONE_NUMBER} > 0"

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    val thumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

                    if (hasPhoneNumber > 0) {
                        val phoneNumberProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phoneNumberSelection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
                        val phoneNumberSelectionArgs = arrayOf(contactId.toString())
                        val phoneNumberCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            phoneNumberProjection,
                            phoneNumberSelection,
                            phoneNumberSelectionArgs,
                            null
                        )
                        phoneNumberCursor?.use { phoneCursor ->
                            if (phoneCursor.moveToFirst()) {
                                val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                val contact = Contact(displayName = displayName, number = phoneNumber, imageSrc = thumbnailUri ?: "")
                                contactsList.add(contact)
                            }
                        }
                    } else {
                        continue
                    }
                } while (cursor.moveToNext())
            }
        }
        return contactsList
    }
    
    // TODO: Display an empty RecyclerView if we have not received access or contacts
    // If we have been given access and received contacts, set them in the RecyclerView,
    // otherwise display our test contacts
    private fun setRecyclerViewContent() {
        val composeView = requireView().findViewById<ComposeView>(R.id.compose_view)
        if (importedContacts.isNotEmpty()) {
            composeView.setContent {RecyclerView(importedContacts)}
        } else {
            composeView.setContent {RecyclerView(testContacts)}
        }
    }

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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun RecyclerView(contacts: List<Contact>) {
        LazyColumn(modifier = Modifier.padding(vertical = 0.dp)) {
            // TODO: Move this to view model
            val grouped = contacts.groupBy {  it.displayName[0]}

            grouped.forEach { (initial, contacts) ->
                stickyHeader {
                    CharacterHeader(character = initial)
                }

                items(contacts) { contact ->
                    ListItem(
                        circleText = contact.displayName.take(1),
                        text = contact.displayName,
                        modifier = Modifier.fillMaxWidth()
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
        RecyclerView(testContacts)
    }

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