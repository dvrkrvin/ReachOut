package com.lincolnstewart.android.reachout.ui.setup.tabs

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lincolnstewart.android.reachout.ContactRepository
import com.lincolnstewart.android.reachout.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "ChildOneViewModel"

// Shared ViewModel for contacts list and and add contact fragment
class ChildOneViewModel : ViewModel() {
    private val contactRepository = ContactRepository.get()

    //TODO: Fix application crashing if Contact has an empty data member

    var importedContacts = mutableListOf<Contact>()

    val selectedContacts = mutableMapOf<UUID, Boolean>()

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
        viewModelScope.launch {
            val newContact = Contact(
                id = contact.id,
                displayName = contact.displayName,
                number = contact.number
            )
            repoAddContact(newContact)
        }
    }

    fun addContacts(contactList: List<Contact>) {
        Log.d(TAG, "${contactList.count()} contacts about to be added")

        viewModelScope.launch {
            repoAddContacts(contactList)
        }
    }

    suspend fun removeContacts(selectedContactUUIDList: List<UUID>) {
        viewModelScope.launch {
            repoRemoveContact(selectedContactUUIDList)
        }
    }

    fun loadContacts(): Flow<List<Contact>> {
        return contactRepository.getContacts()
    }

    private suspend fun repoAddContact(contact: Contact) {
        contactRepository.addContact(contact)
    }

    private suspend fun repoAddContacts(contactList: List<Contact>) {
        contactRepository.addContacts(contactList)
    }

    private suspend fun repoRemoveContact(selectedContactUUIDList: List<UUID>) {
        contactRepository.removeSelectedContacts(selectedContactUUIDList)
    }
}