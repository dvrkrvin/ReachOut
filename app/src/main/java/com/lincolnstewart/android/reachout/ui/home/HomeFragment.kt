package com.lincolnstewart.android.reachout.ui.home

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
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
import java.util.*
import java.util.concurrent.TimeUnit

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

    override fun onResume() {
        super.onResume()
        setProgressBar()
        setUserStats()
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

        if (quotes.isNotEmpty()) {
            quoteCycleJob = lifecycleScope.launch {
                delay(1250)
                while (true) {

                    //TODO: This will crash if the quotes haven't been retrieved in time
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
        } else {
            // Unable to retrieve quotes, show static quote
            binding.quotesTextView.text = """"No act of kindness, no matter how small, is ever wasted.""""
            binding.quoteAuthorView.text = "- Aesop"
            binding.quotesTextView.visibility = View.VISIBLE
            binding.quoteAuthorView.visibility = View.VISIBLE
        }

    }

    private fun setProgressBar() {
        // Get views
        val progressBar = binding.progressBar
        val progressText = binding.xpTextView

        // Get users current level progress
        val totalUserXp = getUserXp()
        val userCurrentLevel = viewModel.checkUserLevel(totalUserXp)
        val minXpForLevel = viewModel.getMinXPForLevel(userCurrentLevel)
        var currentLevelProgress = totalUserXp - minXpForLevel
        Log.d(TAG, "Current Level Progress: $currentLevelProgress")

        // Get required xp for next level
        val maxXpForLevel = viewModel.getMaxXPForLevel(userCurrentLevel)
        Log.d(TAG, "Max XP for level: $maxXpForLevel")

        val actualNeededXp = maxXpForLevel - minXpForLevel

        // If user has maxed out xp, set their progress to the max
        if (currentLevelProgress > actualNeededXp) {
            currentLevelProgress = actualNeededXp
        }

        // Set progress text
        val builtProgressString = "$currentLevelProgress / $actualNeededXp"
        progressText.text = builtProgressString

        //  Set progress bar max value
        progressBar.max = actualNeededXp


        // Animate progress bar to user's current xp level
        progressBar.progress = 0                                                                //THIS
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, currentLevelProgress)
        animator.duration = 1000

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000

        // Set a listener to update the progress value as the animation progresses
        valueAnimator.addUpdateListener {
                                                        //THIS
            val progress = (it.animatedValue as Float * currentLevelProgress).toInt()
            progressBar.progress = progress
        }

        // Start both animators
        animator.start()
        valueAnimator.start()
    }

    private fun setUserStats() {
        manageUserLevel()
        manageDaysSinceStat()
        manageMonthlyStat()
    }

    private fun getUserXp() : Int {
        //Get user's current xp
        val sharedPreferences = context?.getSharedPreferences("StatPrefs", Context.MODE_PRIVATE)
        val currentUserXp = sharedPreferences?.getString("user_xp", "0")
        Log.d(TAG, "User XP Retrieved from StatPrefs: $currentUserXp")
        return currentUserXp?.toInt() ?: 0
    }

    private fun manageUserLevel() {
        // Get user's current level and set it to the view
        val userLevelCountView = binding.levelTextView
        val userXp = getUserXp()
        val userLevel = viewModel.checkUserLevel(userXp)
        val userLevelString = userLevel.toString()
        userLevelCountView.text = userLevelString
    }

    private fun manageDaysSinceStat() {
        //Get user's last reachout time, calculate the difference, and set it in the view
        val daysSinceLastCountView = binding.sinceLastTextView

        val sharedPreferences = context?.getSharedPreferences("StatPrefs", Context.MODE_PRIVATE)
        val lastReachoutTime = sharedPreferences?.getLong("last_reachout_time", 0)
        if (lastReachoutTime == 0L) {
            daysSinceLastCountView.text = "0"
        } else {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastAction = currentTime - lastReachoutTime!!
            val daysSinceLastAction = TimeUnit.MILLISECONDS.toDays(timeSinceLastAction)
            daysSinceLastCountView.text = daysSinceLastAction.toString()
        }
    }

    private fun manageMonthlyStat() {
        val sharedPreferences = context?.getSharedPreferences("StatPrefs", Context.MODE_PRIVATE)

        // Get current month
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

        // Get last month
        val lastMonth = sharedPreferences?.getInt("last_month", 0)

        // If the current month is different from the last month, reset the monthly reachout count
        if (currentMonth != lastMonth) {
            val editor = sharedPreferences?.edit()
            editor?.putInt("last_month", currentMonth)
            editor?.putInt("monthly_reachouts", 0)
            editor?.apply()
        }

        // Get monthly reachout count and set to view
        val monthlyReachoutsCountView = binding.thisMonthTextView

        val monthlyReachouts = sharedPreferences?.getInt("monthly_reachouts", 0)
        monthlyReachoutsCountView.text = monthlyReachouts.toString()
    }

}