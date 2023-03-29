package com.lincolnstewart.android.reachout.ui.resources.tabs

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResourceChildTwoViewModel : ViewModel() {

    val videoLinks = mutableListOf<String>()

    fun retrieveVideoLinks() {
        // This block of code is for retrieving data from Firebase
        val database = FirebaseDatabase.getInstance()
        val videoLinksRef = database.getReference("resources/videoLinks")

        val videoLinksListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val link = childSnapshot.getValue(String::class.java)
                    if (link != null) {
                        videoLinks.add(link)
                    }
                }
                Log.d(TAG, "Video links retrieved: ${videoLinks.count()}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MyApp", "Error retrieving video links: $databaseError")
            }
        }

        videoLinksRef.addValueEventListener(videoLinksListener)
        // End of Firebase data retrieval block
    }
}