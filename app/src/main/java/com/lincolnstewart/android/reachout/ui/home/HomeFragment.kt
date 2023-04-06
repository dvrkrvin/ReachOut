package com.lincolnstewart.android.reachout.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.databinding.FragmentHomeBinding
import com.lincolnstewart.android.reachout.model.Quote
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private var quoteCycleJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveQuotes()
        startQuoteDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        quoteCycleJob?.cancel()
        _binding = null
    }

    // This is for retrieving data from Firebase
    private fun retrieveQuotes() {
        val database = FirebaseDatabase.getInstance()
        val quotesRef = database.getReference("quotes")

        val quotesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val author = childSnapshot.key
                    val quoteText = childSnapshot.getValue(String::class.java)
                    if (author != null && quoteText != null) {

                        // Create Article object and add it to the list
                        lifecycleScope.launch {
                            val quote = Quote(author, quoteText)
                            viewModel.quotes.add(quote)
                        }
                    }
                }
                Log.d(TAG, "Quotes received: ${viewModel.quotes.count()}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error retrieving quotes: $databaseError")
            }
        }
        quotesRef.addValueEventListener(quotesListener)
    }

    private fun startQuoteDisplay() {
        val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.long_fade_in)
        val fadeOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.long_fade_out)
        val quotes = viewModel.quotes
        var index = 0

        quoteCycleJob = lifecycleScope.launch {
            delay(1250)
            while (true) {
                val quote = quotes[index % quotes.size]
                binding.quotesTextView.text = """"${quote.quoteText}""""
                binding.quoteAuthorView.text = "- ${quote.author}"
                binding.quotesTextView.startAnimation(fadeInAnim)
                binding.quoteAuthorView.startAnimation(fadeInAnim)
                binding.quotesTextView.visibility = View.VISIBLE
                binding.quoteAuthorView.visibility = View.VISIBLE
                delay(7000)
                binding.quotesTextView.startAnimation(fadeOutAnim)
                binding.quoteAuthorView.startAnimation(fadeOutAnim)
                binding.quotesTextView.visibility = View.INVISIBLE
                binding.quoteAuthorView.visibility = View.INVISIBLE
                delay(1250)
                index++
            }
        }
    }

}