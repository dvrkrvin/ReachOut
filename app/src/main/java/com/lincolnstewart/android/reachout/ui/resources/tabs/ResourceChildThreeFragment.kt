package com.lincolnstewart.android.reachout.ui.resources.tabs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.ReachOutApplication
import com.lincolnstewart.android.reachout.databinding.FragmentResourceChildThreeBinding
import kotlinx.coroutines.delay
import java.util.*

class ResourceChildThreeFragment : Fragment() {

    companion object {
        fun newInstance() = ResourceChildThreeFragment()
    }

    private var _binding: FragmentResourceChildThreeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ResourceChildThreeViewModel

    // Get application class instance
    private val appContext: ReachOutApplication by lazy {
        requireActivity().applicationContext as ReachOutApplication
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResourceChildThreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResourceChildThreeViewModel::class.java)

        setRecyclerViewContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startActivityWithQuery(query: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=$query")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            val webSearchUri = Uri.parse("https://www.google.com/search?q=$query")
            val webSearchIntent = Intent(Intent.ACTION_VIEW, webSearchUri)
            startActivity(webSearchIntent)
        }
    }

    private fun setRecyclerViewContent() {
        val composeView = requireView().findViewById<ComposeView>(R.id.search_compose_view)
        composeView.setContent { RecyclerView(viewModel.searchTerms) }
    }

    @Composable
    fun SearchListItem(searchTerm: String) {
        var isTapped by remember { mutableStateOf(false) }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(62.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            startActivityWithQuery(searchTerm)
                            isTapped = true
                        }
                    )
                }
                .background(color = if (isTapped) Color.LightGray else Color.White )
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_search_24),
                contentDescription = null,
                alpha = 0.5f
            )
            Column {
                Text(
                    text = searchTerm,
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
    fun RecyclerView(searchTerms : List<String>) {
        LazyColumn(modifier = Modifier.padding(vertical = 0.dp)) {
            items(searchTerms) { searchTerm ->
                SearchListItem(
                    searchTerm,
                )
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }

    @Preview
    @Composable
    fun PreviewSearchListItem() {
        SearchListItem(
            "Restaurants"
        )
    }

}