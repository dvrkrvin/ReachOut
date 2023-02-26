package com.lincolnstewart.android.reachout

import android.content.Context
import androidx.room.Room
import com.lincolnstewart.android.reachout.database.ContactDatabase
import com.lincolnstewart.android.reachout.model.Contact
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

    suspend fun getContacts(): List<Contact> = database.contactDao().getContacts()

    suspend fun addContact(contact: Contact) {
        database.contactDao().addContact(contact)
    }

    suspend fun removeSelectedContacts(selectedContactUUIDList: List<UUID>) {
        database.contactDao().deleteContactsById(selectedContactUUIDList)
    }

//    suspend fun getContact(id: UUID): Crime = database.crimeDao().getCrime(id)

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