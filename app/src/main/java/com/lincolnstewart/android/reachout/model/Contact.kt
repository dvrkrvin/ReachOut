package com.lincolnstewart.android.reachout.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity data class Contact(
    @PrimaryKey val id: UUID,
    val displayName: String,
    val number: String,
//    val imageSrc: String
    )
