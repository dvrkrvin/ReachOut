package com.lincolnstewart.android.reachout.ui.resources.tabs

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.databinding.FragmentReachBinding
import com.lincolnstewart.android.reachout.databinding.FragmentResourceChildTwoBinding
import com.lincolnstewart.android.reachout.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class ResourceChildTwoFragment : Fragment() {

    companion object {
        fun newInstance() = ResourceChildTwoFragment()
    }

    private var _binding: FragmentResourceChildTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ResourceChildTwoViewModel
    private var videoJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResourceChildTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ResourceChildTwoViewModel::class.java)

        super.onViewCreated(view, savedInstanceState)

        retrieveVideoLinks()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Cancel chooseRandomContact if user leaves fragment before the job is complete
        videoJob?.cancel()
        _binding = null
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
                        videoJob = lifecycleScope.launch {
                            val title = getWebPageTitle(link)
//                            val image = getWebPageMainImage(link)

                            val video = Video(UUID.randomUUID(), title, link)

                            viewModel.videos.add(video)
                            setRecyclerViewContent()
                        }

                    }
                }
                Log.d(com.lincolnstewart.android.reachout.ui.resources.TAG, "Video links received: ${viewModel.videos.count()}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error retrieving video links: $databaseError")
            }
        }

        videoLinksRef.addValueEventListener(videoLinksListener)
        // End of Firebase data retrieval block
    }

    suspend fun getWebPageTitle(url: String): String = withContext(Dispatchers.IO) {
        val document = Jsoup.connect(url).get()
        return@withContext document.title()
    }

    // This function is not in use
    suspend fun getWebPageMainImage(url: String): String? {
        return withContext(Dispatchers.IO) {
            val document: Document = Jsoup.connect(url).get()

            val metaImage = document.select("meta[property=og:image]").attr("content")

            if (metaImage.isNotEmpty()) {
                metaImage
            } else {
                val images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]")
                if (images.size > 0) {
                    images[0].absUrl("src")
                } else {
                    null
                }
            }
        }
    }

    private fun followLink(link: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }

    private fun setRecyclerViewContent() {
        val composeView = requireView().findViewById<ComposeView>(R.id.video_compose_view)
        composeView.setContent { RecyclerView(viewModel.videos) }
    }

    @Composable
    fun VideoListItem(video: Video) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(64.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            followLink(video.link, requireContext())
                        }
                    )
                }
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                contentDescription = null
            )
            Column {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
//                Text(
//                    text = article.description,
//                    style = MaterialTheme.typography.body2,
//                    textAlign = TextAlign.Start
//                )
            }
        }
    }

    @Composable
    fun RecyclerView(videos: List<Video>) {
        LazyColumn(modifier = Modifier.padding(vertical = 0.dp)) {
            items(videos) { video ->
                VideoListItem(
                    video = video,
                )
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }

    @Preview
    @Composable
    fun PreviewVideoListItem() {
        VideoListItem(
            video = Video(
                id = UUID.randomUUID(),
                title = "Video Title",
                link = "https://www.google.com",
            )
        )
    }

}