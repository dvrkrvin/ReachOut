package com.lincolnstewart.android.reachout.ui.alarm

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}