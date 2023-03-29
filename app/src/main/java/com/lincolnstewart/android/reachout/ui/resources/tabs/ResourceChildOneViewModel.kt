package com.lincolnstewart.android.reachout.ui.resources.tabs

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResourceChildOneViewModel : ViewModel() {

    val articleLinks = mutableListOf<String>()

    fun retrieveArticleLinks() {
        // This block of code is for retrieving data from Firebase
        val database = FirebaseDatabase.getInstance()
        val articleLinksRef = database.getReference("resources/articleLinks")

        val articleLinksListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val link = childSnapshot.getValue(String::class.java)
                    if (link != null) {
                        articleLinks.add(link)
                    }
                }
                Log.d(com.lincolnstewart.android.reachout.ui.resources.TAG, "Article links received: ${articleLinks.count()}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MyApp", "Error retrieving article links: $databaseError")
            }
        }

        articleLinksRef.addValueEventListener(articleLinksListener)
        // End of Firebase data retrieval block
    }
}