package com.lincolnstewart.android.reachout.ui.home

import androidx.lifecycle.ViewModel
import com.lincolnstewart.android.reachout.model.Quote

class HomeViewModel : ViewModel() {

    val quotes = mutableListOf<Quote>()

    fun checkUserLevel(userXp: Int): Int {
        when (userXp) {
            in 0..199 -> {
                return 1
            }
            in 200..399 -> {
                // Level 2
                return 2
            }
            in 400..599 -> {
                // Level 3
                return 3
            }
            in 600..799 -> {
                // Level 4
                return 4
            }
            in 800..999 -> {
                // Level 5
                return 5
            }
            in 1000..1299 -> {
                // Level 6
                return 6
            }
            in 1300..1599 -> {
                // Level 7
                return 7
            }
            in 1600..1899 -> {
                // Level 8
                return 8
            }
            in 1900..2199 -> {
                // Level 9
                return 9
            }
            else -> {
                // Level 10
                return 10
            }
        }
    }

    fun getMinXPForLevel(userLevel: Int) : Int{
        when (userLevel) {
            1 -> {
                return 0
            }
            2 -> {
                // Level 2
                return 200
            }
            3 -> {
                // Level 3
                return 400
            }
            4 -> {
                // Level 4
                return 600
            }
            5 -> {
                // Level 5
                return 800
            }
            6 -> {
                // Level 6
                return 1000
            }
            7 -> {
                // Level 7
                return 1300
            }
            8 -> {
                // Level 8
                return 1600
            }
            9 -> {
                // Level 9
                return 1900
            }
            10 -> {
                // Level 10
                return 2200
            }
        }
        return -1
    }

    fun getMaxXPForLevel(userLevel: Int) : Int {
        when (userLevel) {
            1 -> {
                return 200
            }
            2 -> {
                // Level 2
                return 400
            }
            3 -> {
                // Level 3
                return 600
            }
            4 -> {
                // Level 4
                return 800
            }
            5 -> {
                // Level 5
                return 1000
            }
            6 -> {
                // Level 6
                return 1300
            }
            7 -> {
                // Level 7
                return 1600
            }
            8 -> {
                // Level 8
                return 1900
            }
            9 -> {
                // Level 9
                return 2200
            }
            10 -> {
                // Level 10
                return 2400
            }
        }
        return -1
    }
}