package com.lincolnstewart.android.reachout.ui.reach

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.databinding.FragmentReachBinding
import com.lincolnstewart.android.reachout.model.Contact
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "ReachFragment"

class ReachFragment : Fragment() {

    private var _binding: FragmentReachBinding? = null
    private val binding get() = _binding!!

    private val reachViewModel: ReachViewModel by viewModels()
    private var crcJob: Job? = null

    private val sendSmsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(TAG, "SMS Result: $result")
        // This check always returns cancelled. May be emulator related.
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show()
            //TODO: Award xp

        } else {
            Toast.makeText(context, "SMS sending failed", Toast.LENGTH_SHORT).show()
        }
    }

    private val makeCallLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        println("Call Result: $result")

        // This check always returns cancelled. May be emulator related.
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(context, "Call started successfully", Toast.LENGTH_SHORT).show()
            //TODO: Award xp

        } else {
            Toast.makeText(context, "Call failed to start", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReachBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Cancel chooseRandomContact if user leaves fragment before the job is complete
        crcJob?.cancel()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.callButton.setOnClickListener {
            redirectForCall()
        }
        binding.textButton.setOnClickListener {
            redirectForText()
        }
        initiateChallenge()
    }

    private fun initiateChallenge() {
        chooseRandomContact()

    }

    private fun chooseRandomContact() {
        println("CRC launched")

        crcJob = lifecycleScope.launch{

            reachViewModel.loadContacts().collect {

                val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.extra_long_fade_in)
                binding.nameText.startAnimation(fadeInAnimation)
                binding.nameText.visibility = View.VISIBLE
                val nameText = binding.nameText

                var selectedContact: Contact? = null
                val random = Random()
                for (i in 0 until 25) { // browse over 25 contacts before stopping
                    selectedContact = it[random.nextInt(it.size)]
                    nameText.text = selectedContact.displayName
                    delay(75) // delay of 75 milliseconds for visibility
                }
                println("Selected Contact: $selectedContact")
                if (selectedContact != null) {
                    reachViewModel.setSelectedPhoneNumber(selectedContact.number)
                }
                delay(500)
                showCallToAction()
                delay(750)
                showButtons()
            }
        }
    }

    private fun showCallToAction() {
        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.medium_fade_in)
        binding.callToActionText.startAnimation(fadeInAnimation)
        binding.callToActionText.visibility = View.VISIBLE
    }

    private fun showButtons() {
        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.medium_fade_in)
        binding.callButton.startAnimation(fadeInAnimation)
        binding.textButton.startAnimation(fadeInAnimation)
        binding.callButton.visibility = View.VISIBLE
        binding.textButton.visibility = View.VISIBLE
    }

    private fun redirectForCall() {
        val recipientPhoneNumber = reachViewModel.getSelectedPhoneNumber()
        val uri = Uri.parse("tel:$recipientPhoneNumber")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        makeCallLauncher.launch(intent)
    }

    private fun redirectForText() {
        val recipientPhoneNumber = reachViewModel.getSelectedPhoneNumber()
//        val message = "Hello!"
        val uri = Uri.parse("smsto:$recipientPhoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
//            .apply { putExtra("sms_body", message) }
        sendSmsLauncher.launch(intent)

    }
}