package com.lincolnstewart.android.reachout.ui.setup.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lincolnstewart.android.reachout.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.lincolnstewart.android.reachout.databinding.FragmentChildOneBinding
import com.lincolnstewart.android.reachout.model.Contact

const val TAG = "ChildOneFragment"
val testContacts = mutableListOf(
    Contact("Alice", "123-456-7890"),
    Contact("Bob", "234-567-8901"),
    Contact("Charlie", "345-678-9012"),
    Contact("Dave", "456-789-0123"),
    Contact("Eve", "567-890-1234"),
    Contact("Frank", "678-901-2345"),
    Contact("Grace", "789-012-3456"),
    Contact("Heidi", "890-123-4567"),
    Contact("Ivan", "901-234-5678"),
    Contact("Alice", "123-456-7890"),
    Contact("Bob", "234-567-8901"),
    Contact("Charlie", "345-678-9012"),
    Contact("Dave", "456-789-0123"),
    Contact("Eve", "567-890-1234"),
    Contact("Frank", "678-901-2345"),
    Contact("Grace", "789-012-3456"),
    Contact("Heidi", "890-123-4567"),
    Contact("Ivan", "901-234-5678"),
    Contact("Julia", "012-345-6789")
)

class ChildOneFragment : Fragment() {

    private var _binding: FragmentChildOneBinding? = null
    private val binding get() = _binding!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val composeView = requireView().findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            RecyclerView(testContacts)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

@Composable
fun ListItem(
    image: Painter,
    text: String,
    modifier: Modifier = Modifier,
    imageSize: Dp = 48.dp,
    textPadding: Dp = 64.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = image,
            contentDescription = "",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .align(Alignment.CenterStart) // Align text with the image
                .padding(start = textPadding)
        )
    }
}

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun RecyclerView(contacts: List<Contact>) {
        LazyColumn(modifier = Modifier.padding(vertical = 0.dp)) {
            // FIXME: Move this to view model
            val grouped = contacts.groupBy {  it.name[0]}

            grouped.forEach { initial, contacts ->
                stickyHeader {
                    CharacterHeader(character = initial)
                }


                items(contacts) { contact ->
                    ListItem(image = painterResource(id = R.drawable.ic_launcher_background), text = contact.name)
                }
            }
        }
    }

    @Composable
    fun CharacterHeader(character: Char) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(vertical = 2.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = character.toString(),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        RecyclerView(testContacts)
    }

    private fun showToast(text: String) {
        Toast.makeText(binding.root.context, text, Toast.LENGTH_SHORT).show()
    }
}

// List item from tutorial, holding onto for reference
//    @Composable
//    fun ListItem(name: String) {
//        Surface(color = MaterialTheme.colors.primary,
//        modifier = Modifier.padding(vertical = 2.dp, horizontal = 0.dp)) {
//            Column(modifier = Modifier
//                .padding(20.dp)
//                .fillMaxWidth()) {
//                Row {
//                    Column(
//                        modifier = Modifier
//                            .weight(1f)
//                    ) {
//                        Text(text = "Course")
//                        Text(text = name, style = MaterialTheme.typography.h4.copy(
//                            fontWeight = FontWeight.ExtraBold
//                        ))
//                    }
//                    OutlinedButton(onClick = { showToast(name)}) {
//                        Text(text = "Show more")
//                    }
//                }
//            }
//        }
//    }