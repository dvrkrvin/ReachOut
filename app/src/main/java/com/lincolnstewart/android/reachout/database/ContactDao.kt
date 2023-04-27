package com.lincolnstewart.android.reachout.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lincolnstewart.android.reachout.model.Contact
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    fun getContacts(): Flow<List<Contact>>

    @Insert
    suspend fun addContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addContacts(contactList: List<Contact>)

    @Query("DELETE FROM contact WHERE id IN (:idList)")
    suspend fun deleteContactsById(idList: List<UUID>)

}