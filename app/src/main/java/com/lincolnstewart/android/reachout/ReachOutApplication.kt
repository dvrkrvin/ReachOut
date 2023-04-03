package com.lincolnstewart.android.reachout

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lincolnstewart.android.reachout.model.Article
import com.lincolnstewart.android.reachout.model.Video
import com.lincolnstewart.android.reachout.ui.alarm.AlarmItem
import com.lincolnstewart.android.reachout.ui.alarm.AndroidAlarmScheduler
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.util.*

private const val TAG = "ReachOutApplication"

private val coroutineScope = CoroutineScope(Dispatchers.IO)

class ReachOutApplication : Application() {

    var cachedArticles = mutableListOf<Article>()
    var cachedVideos = mutableListOf<Video>()

    private var articleJob: Job? = null
    private var videoJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        ContactRepository.initialize(this)

        // TODO: Why are we scheduling notifications here?
        createNotificationChannel()
        scheduleNotifications()

        // Retrieve and cache article and video links from Firebase
        retrieveArticleLinks()
        retrieveVideoLinks()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReachOutNotificationService.REACHOUT_CHANNEL_ID,
                "ReachOut",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for ReachOut notifications"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotifications() {
        val scheduler = AndroidAlarmScheduler(this)
        val alarmItem = AlarmItem(
            time = LocalDateTime.now(),
            message = "a Friend"
        )
        alarmItem.let(scheduler::schedule)

        Log.d(TAG, "Alarm Scheduled")
    }

    // TODO: If I have time, this code should be in a separate class.
    //region Network Calls
    // Network call to retrieve and cache article links from Firebase
    private fun retrieveArticleLinks(){
        // This block of code is for retrieving data from Firebase
        val database = FirebaseDatabase.getInstance()
        val articleLinksRef = database.getReference("resources/articleLinks")

        val articleLinksListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val link = childSnapshot.getValue(String::class.java)
                    if (link != null) {

                        // Create Article object and add it to the list
                        articleJob = coroutineScope.launch {
                            val title = getWebPageTitle(link)
                            val article = Article(UUID.randomUUID(), title, link)
                            cachedArticles.add(article)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error retrieving article links: $databaseError")
            }
        }

        articleLinksRef.addValueEventListener(articleLinksListener)

        // End of Firebase data retrieval block
    }

    suspend fun getWebPageTitle(url: String): String = withContext(Dispatchers.IO) {
        val document = Jsoup.connect(url).get()
        return@withContext document.title()
    }

    private fun retrieveVideoLinks() {
        // This block of code is for retrieving data from Firebase
        val database = FirebaseDatabase.getInstance()
        val videoLinksRef = database.getReference("resources/videoLinks")

        val videoLinksListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val link = childSnapshot.getValue(String::class.java)
                    if (link != null) {

                        // Create Video object and add it to the list
                        videoJob = coroutineScope.launch {
                            val title = getWebPageTitle(link)
//                            val image = getWebPageMainImage(link)

                            val video = Video(UUID.randomUUID(), title, link)

                            cachedVideos.add(video)
                        }

                    }
                }
                Log.d(TAG, "Video links received: ${cachedVideos.size}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error retrieving video links: $databaseError")
            }
        }

        videoLinksRef.addValueEventListener(videoLinksListener)
        // End of Firebase data retrieval block
    }
    //endregion

}