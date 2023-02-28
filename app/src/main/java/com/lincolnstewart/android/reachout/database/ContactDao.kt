package com.lincolnstewart.android.reachout.database

import androidx.room.*
import com.lincolnstewart.android.reachout.model.Contact
import java.util.*

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    suspend fun getContacts(): List<Contact>

    @Insert
    suspend fun addContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addContacts(contactList: List<Contact>)

    @Query("DELETE FROM contact WHERE id IN (:idList)")
    suspend fun deleteContactsById(idList: List<UUID>)

//    @Query("SELECT * FROM crime WHERE id=(:id)")
//    suspend fun getCrime(id: UUID): Crime
}