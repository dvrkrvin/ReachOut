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
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.ReachOutApplication
import com.lincolnstewart.android.reachout.databinding.FragmentResourceChildOneBinding
import com.lincolnstewart.android.reachout.model.Article
import com.lincolnstewart.android.reachout.ui.resources.ResourcesViewModel
import java.util.*

const val TAG = "ResourceChildOneFragment"

// Helpful articles tab
class ResourceChildOneFragment : Fragment() {

    companion object {
        fun newInstance() = ResourceChildOneFragment()
    }

    private var _binding: FragmentResourceChildOneBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ResourceChildOneViewModel

    // Get application class instance
    private val appContext: ReachOutApplication by lazy {
        requireActivity().applicationContext as ReachOutApplication
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ResourceChildOneViewModel::class.java)
        _binding = FragmentResourceChildOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerViewContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun followLink(link: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }

    private fun setRecyclerViewContent() {
        Log.d(TAG, appContext.cachedArticles.toString())
        val composeView = requireView().findViewById<ComposeView>(R.id.video_compose_view)
        composeView?.setContent { RecyclerView(appContext.cachedArticles) }
    }

    // This function is not in use
//    suspend fun getWebPageMainImage(url: String): String? {
//        return withContext(Dispatchers.IO) {
//            val document: Document = Jsoup.connect(url).get()
//
//            val metaImage = document.select("meta[property=og:image]").attr("content")
//
//            if (metaImage.isNotEmpty()) {
//                metaImage
//            } else {
//                val images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]")
//                if (images.size > 0) {
//                    images[0].absUrl("src")
//                } else {
//                    null
//                }
//            }
//        }
//    }

    //region Composable functions
    @Composable
    fun ArticleListItem(article: Article) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(64.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            followLink(article.link, requireContext())
                        }
                    )
                }
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_article_24),
                contentDescription = null
            )
            Column {
                Text(
                    text = article.title,
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
    fun RecyclerView(articles: List<Article>) {
        LazyColumn(modifier = Modifier.padding(vertical = 0.dp)) {
            items(articles) { article ->
                ArticleListItem(
                    article = article,
                )
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }


    @Preview
    @Composable
    fun PreviewArticleListItem() {
        ArticleListItem(
            article = Article(
                id = UUID.randomUUID(),
                title = "Article Title",
                link = "https://www.google.com"
            )
        )
    }
    //endregion
}