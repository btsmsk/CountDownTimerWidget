package com.bsimsek.countdowntimer_custom_ui_component.countdowntimerview

interface OnCountDownTimerEventListener {
    fun onCountDownTimerStarted()
    fun onCountDownTimerStopped()
    fun onCountDownTimerRunning(remainingTime: Long)
}
