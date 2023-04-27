package com.lincolnstewart.android.reachout

import android.content.Context
import androidx.room.Room
import com.lincolnstewart.android.reachout.database.ContactDatabase
import com.lincolnstewart.android.reachout.model.Contact
import kotlinx.coroutines.flow.Flow
import java.util.*

private const val DATABASE_NAME = "contact-database"

class ContactRepository private constructor(context: Context) {

    private val database: ContactDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            ContactDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    fun getContacts(): Flow<List<Contact>> = database.contactDao().getContacts()

    suspend fun addContact(contact: Contact) {
        database.contactDao().addContact(contact)
    }

    suspend fun addContacts(contactList: List<Contact>) {
        database.contactDao().addContacts(contactList)
    }

    suspend fun removeSelectedContacts(selectedContactUUIDList: List<UUID>) {
        database.contactDao().deleteContactsById(selectedContactUUIDList)
    }

    companion object {
        private var INSTANCE: ContactRepository? =  null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = ContactRepository(context)
            }
        }

        fun get(): ContactRepository {
            return  INSTANCE ?:
            throw IllegalStateException("ContactRepository must be initialized")
        }


    }
}