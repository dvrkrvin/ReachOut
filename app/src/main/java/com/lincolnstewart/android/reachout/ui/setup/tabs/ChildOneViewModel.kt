package com.lincolnstewart.android.reachout.ui.setup.tabs

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation.findNavController
import com.lincolnstewart.android.reachout.ContactRepository
import com.lincolnstewart.android.reachout.model.Contact
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.log

private const val TAG = "ChildOneViewModel"

// Shared ViewModel for contacts list and and add contact fragment
class ChildOneViewModel : ViewModel() {
    private val contactRepository = ContactRepository.get()

    //TODO: Fix application crashing if Contact has an empty data member
    var importedContacts = mutableListOf<Contact>()

//    val testContacts = mutableListOf(
//        Contact("Alice Dave", "123-456-7890"),
//        Contact("Barrett Johann", "123-456-1234"),
//        Contact("Charlie Brown",  "345-678-9012"),
//        Contact("Dave Fill",  "456-789-0123"),
//        Contact("Eve Sert",  "567-890-1234"),
//        Contact("Frank Gora",  "678-901-2345"),
//        Contact("Grace Tree",  "789-012-3456"),
//        Contact("Julia Bell", "012-345-6789"),
//        Contact("Julia Bell", "012-345-6789"),
//        Contact("Barrett Flip", "123-456-1234"),
//        Contact("Charlie Joe",  "345-678-9012"),
//        Contact("Dave Davidson",  "456-789-0123"),
//        Contact("Dave Moore", "456-789-0123"),
//        Contact("Eve Tea",  "567-890-1234"),
//        Contact("Frank Strog",  "678-901-2345")
//    )

    // Read contacts from users native contacts list
    fun readContacts(context: Context): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contentResolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
//            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
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
//                    val thumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

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
                                val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER))
                                val uuid = UUID.randomUUID()
                                val contact = Contact(id = uuid, displayName = displayName, number = phoneNumber)
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

    fun addContact(contact: Contact) {
//        Log.d(TAG, "$contact about to be added")
//        testContacts.add(contact)
//        Log.d(TAG, testContacts.toString())

        viewModelScope.launch {
            val newContact = Contact(
                id = contact.id,
                displayName = contact.displayName,
                number = contact.number
            )
            repoAddContact(newContact)
        }
    }

    suspend fun loadContacts(): List<Contact> {
        return contactRepository.getContacts()
    }

    private suspend fun repoAddContact(contact: Contact) {
        contactRepository.addContact(contact)
    }
}