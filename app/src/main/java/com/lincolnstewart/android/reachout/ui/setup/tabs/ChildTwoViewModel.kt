package com.lincolnstewart.android.reachout.ui.setup.tabs

import androidx.lifecycle.ViewModel

class ChildTwoViewModel : ViewModel() {
    fun freqStringToSpinnerInt (frequencyString: String) : Int{
        when (frequencyString) {
            "Weekly" -> return 0
            "Bi-Weekly" -> return 1
        }
        return -1
    }

    fun dayStringToSpinnerInt (dayString: String) : Int{
        when (dayString) {
            "Monday" -> return 0
            "Tuesday" -> return 1
            "Wednesday" -> return 2
            "Thursday" -> return 3
            "Friday" -> return 4
            "Saturday" -> return 5
            "Sunday" -> return 6
        }
        return -1
    }
}