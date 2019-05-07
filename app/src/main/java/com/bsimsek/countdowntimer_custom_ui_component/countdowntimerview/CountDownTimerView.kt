package com.bsimsek.countdowntimer_custom_ui_component.countdowntimerview

import android.content.Context
import android.graphics.PorterDuff
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import com.bsimsek.countdowntimer_custom_ui_component.R

data class CountDownTimerViewDataModel(
    val timeTextSize: Float? = null,
    val timeTextColor: Int? = null,
    val descriptionText: String? = null,
    val descriptionTextColor: Int? = null,
    val descriptionTextSize: Float? = null,
    val innerCircleColor: Int? = null,
    val outerCircleColor: Int? = null,
    val clockwise: Boolean? = null,
    val animation: Boolean? = null
)

private fun readAttributes(context: Context, attrs: AttributeSet?): CountDownTimerViewDataModel {
    attrs ?: return CountDownTimerViewDataModel()
    val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.CountDownTimerView, 0, 0)
    return try {
        val timeTextSize = attributes.getFloat(R.styleable.CountDownTimerView_timeTextSize, 40F)
        val timeTextColor = attributes.getResourceId(R.styleable.CountDownTimerView_timeTextColor, R.color.greyDark)
        val descriptionText =
            attributes.getString(R.styleable.CountDownTimerView_descriptionText) ?: "Default Description"
        val descriptionTextColor =
            attributes.getResourceId(R.styleable.CountDownTimerView_descriptionTextColor, R.color.grey)
        val descriptionTextSize = attributes.getFloat(R.styleable.CountDownTimerView_descriptionTextSize, 30F)
        val innerCircleColor = attributes.getResourceId(R.styleable.CountDownTimerView_innerCircleColor, R.color.grey)
        val outerCircleColor =
            attributes.getResourceId(R.styleable.CountDownTimerView_outerCircleColor, R.color.colorYellow)
        val clockwise = attributes.getBoolean(R.styleable.CountDownTimerView_clockwise, false)
        val animation = attributes.getBoolean(R.styleable.CountDownTimerView_animation, false)

        return CountDownTimerViewDataModel(
            timeTextSize, timeTextColor,
            descriptionText, descriptionTextColor, descriptionTextSize,
            innerCircleColor, outerCircleColor,
            clockwise, animation
        )

    } catch (e: Exception) {
        CountDownTimerViewDataModel()
    } finally {
        attributes?.recycle()
    }
}

class CountDownTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), OnCountDownTimerEventListener {

    private lateinit var progressBarCircle: ProgressBar
    private lateinit var textViewTime: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var timer: CountDownTimer
    private var timerCount: Long = 0L
    private var remainingTimeSecond: Long = 0L
    private var countDownTimerEventListener: OnCountDownTimerEventListener? = null
    private lateinit var animation: ObjectAnimator
    private var animationAllowed = false

    init {
        initViews()
        setAttributes(context, attrs)
    }

    private fun initViews() {
        LayoutInflater.from(context).inflate(R.layout.count_down_timer_view, this, true)
        progressBarCircle = this.findViewById(R.id.progressBarCircle)
        textViewTime = this.findViewById(R.id.textViewTime)
        textViewDescription = this.findViewById(R.id.textViewDescription)
    }

    private fun setAttributes(context: Context, attrs: AttributeSet?) {
        val data = readAttributes(context, attrs)

        data.apply {
            textViewDescription.apply {
                text = descriptionText
                setTextColor(ContextCompat.getColor(context, descriptionTextColor!!))
                textSize = descriptionTextSize!!
            }

            textViewTime.apply {
                textSize = timeTextSize!!
                setTextColor(ContextCompat.getColor(context, timeTextColor!!))
            }

            progressBarCircle.apply {
                background.setColorFilter(
                    ContextCompat.getColor(context, innerCircleColor!!),
                    PorterDuff.Mode.SRC_IN
                )

                progressDrawable.setColorFilter(
                    ContextCompat.getColor(context, outerCircleColor!!),
                    PorterDuff.Mode.SRC_IN
                )
            }

            if (clockwise!!) {
                rotateToClockWise()
            }
            animationAllowed = animation!!
        }
    }

    private fun setProgressBarValues() {
        progressBarCircle.apply {
            max = timerCount.toInt() * 1000
            progress = timerCount.toInt() * 1000
        }
    }

    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int) {
        animation = ObjectAnimator.ofInt(pb, "progress", pb.progress, 0)
        animation.apply {
            setAutoCancel(true)
            duration = progressTo.toLong()
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun rotateToClockWise() {
        progressBarCircle.apply {
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            rotation = 90F
        }
    }

    private fun stopCountDownTimer() {
        timer.cancel()
    }

    fun startTimer(timerCount: Long, animation: Boolean = animationAllowed, runClockwise: Boolean = false) {
        this.timerCount = timerCount
        this.animationAllowed = animation
        if (runClockwise) {
            rotateToClockWise()
        }
        setProgressBarValues()
        countDownTimerEventListener?.onCountDownTimerStarted()
        timer = object : CountDownTimer(timerCount * 1000, 1000) {
            override fun onFinish() {
                textViewTime.text = "0"
                setProgressBarValues()
                stopCountDownTimer()
                countDownTimerEventListener?.onCountDownTimerStopped()
            }

            override fun onTick(millisUntilFinished: Long) {
                remainingTimeSecond = millisUntilFinished / 1000
                textViewTime.text = (remainingTimeSecond).toString()
                countDownTimerEventListener?.onCountDownTimerRunning(remainingTimeSecond)
                if (animationAllowed) {
                    setProgressAnimate(progressBarCircle, millisUntilFinished.toInt())
                } else {
                    progressBarCircle.progress = (millisUntilFinished).toInt()
                }
            }
        }.start()
    }

    fun stopTimer() {
        stopCountDownTimer()
    }

    fun resetTimer() {
        if (animationAllowed) {
            animation.cancel()
        }
        stopCountDownTimer()
        startTimer(timerCount)
    }

    fun setDescriptionText(descriptionText: String?) {
        textViewDescription.text = descriptionText
    }

    fun setDescriptionTextColor(descriptionTextColor: Int) {
        textViewDescription.setTextColor(descriptionTextColor)
    }

    fun setDescriptionTextSize(descriptionTextSize: Float) {
        textViewDescription.textSize = descriptionTextSize
    }

    fun setTimeTextSize(timeTextSize: Float) {
        textViewTime.textSize = timeTextSize
    }

    fun setTimeTextColor(timeTextColor: Int) {
        textViewTime.setTextColor(timeTextColor)
    }

    fun setProgressInnerCircleColor(innerCircleColor: Int) {
        progressBarCircle.background.setColorFilter(
            ContextCompat.getColor(context, innerCircleColor),
            PorterDuff.Mode.SRC_IN
        )
    }

    fun setProgressOuterCircleColor(outerCircleColor: Int) {
        progressBarCircle.progressDrawable.setColorFilter(
            ContextCompat.getColor(context, outerCircleColor),
            PorterDuff.Mode.SRC_IN
        )
    }

    fun setCountDownTimerEventListener(onCountDownTimerEventListener: OnCountDownTimerEventListener) {
        this.countDownTimerEventListener = onCountDownTimerEventListener
    }

    override fun onCountDownTimerStopped() {
        this.countDownTimerEventListener?.onCountDownTimerStopped()
    }

    override fun onCountDownTimerRunning(remainingTime: Long) {
        this.countDownTimerEventListener?.onCountDownTimerRunning(remainingTime)
    }

    override fun onCountDownTimerStarted() {
        this.countDownTimerEventListener?.onCountDownTimerStarted()
    }
}