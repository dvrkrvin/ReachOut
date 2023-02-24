package com.lincolnstewart.android.reachout.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lincolnstewart.android.reachout.model.Contact
import java.util.*

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    suspend fun getContacts(): List<Contact>

    @Insert
    suspend fun addContact(contact: Contact)

//    @Query("SELECT * FROM crime WHERE id=(:id)")
//    suspend fun getCrime(id: UUID): Crime
}