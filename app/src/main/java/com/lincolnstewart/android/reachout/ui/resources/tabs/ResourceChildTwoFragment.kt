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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.lincolnstewart.android.reachout.ReachOutApplication
import com.lincolnstewart.android.reachout.databinding.FragmentReachBinding
import com.lincolnstewart.android.reachout.databinding.FragmentResourceChildTwoBinding
import com.lincolnstewart.android.reachout.model.Video
import kotlinx.coroutines.*
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

    // Get application class instance
    private val appContext: ReachOutApplication by lazy {
        requireActivity().applicationContext as ReachOutApplication
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResourceChildTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResourceChildTwoViewModel::class.java)

//        retrieveVideoLinks()
        setRecyclerViewContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        videoJob?.cancel()
        _binding = null
    }

    private fun followLink(link: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }

    private fun setRecyclerViewContent() {
        val composeView = requireView().findViewById<ComposeView>(R.id.video_compose_view)
        composeView.setContent { RecyclerView(appContext.cachedVideos) }
    }

    @Composable
    fun VideoListItem(video: Video) {
        var isTapped by remember { mutableStateOf(false) }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(62.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            followLink(video.link, requireContext())
                            isTapped = true
                        }
                    )
                }
                .background(color = if (isTapped) Color.LightGray else Color.White )
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                contentDescription = null,
                alpha = 0.5f
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
        LaunchedEffect(isTapped) {
            delay(100) // set the duration of the animation here
            isTapped = false
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