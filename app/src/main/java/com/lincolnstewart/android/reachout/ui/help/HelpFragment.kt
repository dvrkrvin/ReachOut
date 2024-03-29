package com.lincolnstewart.android.reachout.ui.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.lincolnstewart.android.reachout.databinding.FragmentHelpBinding

private const val TAG = "HelpFragment"

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private val sendSmsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
    }

    private val makeCallLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.callButton.setOnClickListener {
            redirectForCall()
        }
        binding.textButton.setOnClickListener {
            redirectForText()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun redirectForCall() {
        val recipientPhoneNumber = 988
        val uri = Uri.parse("tel:$recipientPhoneNumber")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        makeCallLauncher.launch(intent)
    }

    private fun redirectForText() {
        val recipientPhoneNumber = 988
        val message = "Hi, I need help."
        val uri = Uri.parse("smsto:$recipientPhoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
            .apply { putExtra("sms_body", message) }
        sendSmsLauncher.launch(intent)

    }
}