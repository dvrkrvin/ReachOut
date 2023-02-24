package com.lincolnstewart.android.reachout.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lincolnstewart.android.reachout.model.Contact

@Database(entities = [ Contact::class ], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}