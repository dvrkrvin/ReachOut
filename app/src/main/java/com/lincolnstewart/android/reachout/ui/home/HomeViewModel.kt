package com.lincolnstewart.android.reachout.ui.home

import androidx.lifecycle.ViewModel
import com.lincolnstewart.android.reachout.model.Quote

class HomeViewModel : ViewModel() {

    val quotes = mutableListOf<Quote>()

}