package com.bsimsek.countdowntimer_custom_ui_component

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.bsimsek.countdowntimer_custom_ui_component.countdowntimerview.CountDownTimerView

class MainActivity : AppCompatActivity() {
    private lateinit var countDownTimerView: CountDownTimerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        countDownTimerView = this.findViewById(R.id.countDownTimerView)
        countDownTimerView.startTimer(
            timerCount = 30,
            onCountDownTimerStarted = {
                Log.d("Timer: ", "Started")
            },
            onCountDownTimerRunning = {
                Log.d("Timer: ", "Running $it")
            },
            onCountDownTimerStopped = {
                Log.d("Timer: ", "Stopped")
            })
    }
}
