package com.lincolnstewart.android.reachout.ui.reach

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.lincolnstewart.android.reachout.ContactRepository
import com.lincolnstewart.android.reachout.model.Contact
import kotlinx.coroutines.flow.Flow

class ReachViewModel : ViewModel() {

    private val contactRepository = ContactRepository.get()

    private var selectedPhoneNumber: String? = null

    fun loadContacts(): Flow<List<Contact>> {
        return contactRepository.getContacts()
    }

    fun setSelectedPhoneNumber(phoneNumber: String) {
        selectedPhoneNumber = phoneNumber
    }

    fun getSelectedPhoneNumber(): String? {
        return selectedPhoneNumber
    }
}