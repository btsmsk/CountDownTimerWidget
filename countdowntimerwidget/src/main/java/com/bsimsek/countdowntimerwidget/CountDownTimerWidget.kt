package com.bsimsek.countdowntimerwidget

import android.animation.ObjectAnimator
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat

data class CountDownTimerWidgetAttributes(
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

private fun readAttributes(
    context: Context,
    attrs: AttributeSet?
): CountDownTimerWidgetAttributes {
    attrs ?: return CountDownTimerWidgetAttributes()
    val attributes =
        context.theme.obtainStyledAttributes(attrs, R.styleable.CountDownTimerWidget, 0, 0)
    val timeTextSize: Float?
    val timeTextColor: Int?
    val descriptionText: String?
    val descriptionTextColor: Int?
    val descriptionTextSize: Float?
    val innerCircleColor: Int?
    val outerCircleColor: Int?
    val clockwise: Boolean?
    val animation: Boolean?
    return try {
        attributes.run {
            timeTextSize =
                getDimension(
                    R.styleable.CountDownTimerWidget_timeTextSize,
                    resources.getDimension(R.dimen.time_font_m)
                )
            timeTextColor =
                getResourceId(R.styleable.CountDownTimerWidget_timeTextColor, R.color.colorPrimary)
            descriptionText = getString(R.styleable.CountDownTimerWidget_descriptionText)
            descriptionTextColor =
                getResourceId(
                    R.styleable.CountDownTimerWidget_descriptionTextColor,
                    R.color.colorGreyDark
                )
            descriptionTextSize =
                getDimension(
                    R.styleable.CountDownTimerWidget_descriptionTextSize,
                    resources.getDimension(R.dimen.time_font_l)
                )
            innerCircleColor =
                getResourceId(R.styleable.CountDownTimerWidget_innerCircleColor, R.color.colorGrey)
            outerCircleColor =
                getResourceId(
                    R.styleable.CountDownTimerWidget_outerCircleColor,
                    R.color.colorPrimary
                )
            clockwise = getBoolean(R.styleable.CountDownTimerWidget_clockwise, false)
            animation = getBoolean(R.styleable.CountDownTimerWidget_animation, true)
        }

        return CountDownTimerWidgetAttributes(
            timeTextSize = timeTextSize,
            timeTextColor = timeTextColor,
            descriptionText = descriptionText,
            descriptionTextColor = descriptionTextColor,
            descriptionTextSize = descriptionTextSize,
            innerCircleColor = innerCircleColor,
            outerCircleColor = outerCircleColor,
            clockwise = clockwise,
            animation = animation
        )
    } catch (e: Exception) {
        CountDownTimerWidgetAttributes()
    } finally {
        attributes?.recycle()
    }
}

class CountDownTimerWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var progressBarCircle: ProgressBar
    private lateinit var textViewTime: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var timer: CountDownTimer
    private var timerCount: Long = 0L
    private var remainingTimeSecond: Long = 0L
    private lateinit var animation: ObjectAnimator
    private var animationAllowed = false
    private var runClockWise = false
    private var _onCountDownTimerStarted: (() -> Unit)? = null
    private var _onCountDownTimerStopped: (() -> Unit)? = null
    private var _onCountDownTimerRunning: ((remainingTime: Long) -> Unit)? = null
    private var isRunning = false

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

    /*
     * set attributes to view data
     */
    private fun setAttributes(context: Context, attrs: AttributeSet?) {
        val data = readAttributes(context, attrs)

        data.apply {
            if (!descriptionText.isNullOrEmpty()) {
                textViewDescription.apply {
                    text = descriptionText
                    setTextColor(ContextCompat.getColor(context, descriptionTextColor!!))
                    textSize = descriptionTextSize!! / resources.displayMetrics.scaledDensity
                    visibility = View.VISIBLE
                }
            }

            textViewTime.apply {
                textSize = timeTextSize!! / resources.displayMetrics.scaledDensity
                setTextColor(ContextCompat.getColor(context, timeTextColor!!))
            }

            progressBarCircle.apply {
                background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(
                            context,
                            innerCircleColor!!
                        ), BlendModeCompat.SRC_ATOP
                    )
                progressDrawable.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(
                            context,
                            outerCircleColor!!
                        ), BlendModeCompat.SRC_ATOP
                    )
            }

            if (clockwise!!) {
                rotateToClockWise()
            }
            animationAllowed = animation!!
        }
    }

    /*
    * Set progress bar initial values
    */
    private fun setProgressBarValues() {
        progressBarCircle.apply {
            max = timerCount.toInt() * 1000
            progress = timerCount.toInt() * 1000
        }
    }

    /*
     * Set the animation to progress bar
     * @param pb: progress bar which will be added animation
     * @param progressTo : duration time that animation will continue
     */
    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int) {
        animation = ObjectAnimator.ofInt(pb, "progress", pb.progress, 0)
        animation.apply {
            setAutoCancel(true)
            duration = progressTo.toLong()
            interpolator = LinearInterpolator()
            start()
        }
    }

    /*
     * Change the working direction of progress bar
     */
    private fun rotateToClockWise() {
        progressBarCircle.apply {
            max = timerCount.toInt() * 1000
            progress = timerCount.toInt() * 1000
        }
    }

    /*
     * Set start timer count and start the Count Down Timer.
     * @param timerCount: timer length value
     * @param animtion: true -> progress run with animation
     *                  false -> progress run standard
     * @param runClockwise: progress running direction
     */
    fun startTimer(
        timerCount: Long,
        animation: Boolean = animationAllowed,
        runClockwise: Boolean = runClockWise,
        onCountDownTimerStarted: (() -> Unit)? = null,
        onCountDownTimerStopped: (() -> Unit)? = null,
        onCountDownTimerRunning: ((remainingTime: Long) -> Unit)? = null
    ) {
        this.timerCount = timerCount
        this.animationAllowed = animation
        this._onCountDownTimerStarted = onCountDownTimerStarted
        this._onCountDownTimerStopped = onCountDownTimerStopped
        this._onCountDownTimerRunning = onCountDownTimerRunning
        if (runClockwise) {
            rotateToClockWise()
        }
        setProgressBarValues()
        _onCountDownTimerStarted?.invoke()
        timer = object : CountDownTimer(timerCount * 1000, 1000) {
            override fun onFinish() {
                textViewTime.text = "0"
                setProgressBarValues()
                stopTimer()
                _onCountDownTimerStopped?.invoke()
            }

            override fun onTick(millisUntilFinished: Long) {
                remainingTimeSecond = millisUntilFinished / 1000
                textViewTime.text = (remainingTimeSecond).toString()
                _onCountDownTimerRunning?.invoke(remainingTimeSecond)
                isRunning = true
                if (animationAllowed) {
                    setProgressAnimate(progressBarCircle, millisUntilFinished.toInt())
                } else {
                    progressBarCircle.progress = (millisUntilFinished).toInt()
                }
            }
        }
        if(!isRunning) timer.start()
    }

    /*
     * Stop count down timer
     */
    fun stopTimer() {
        timer.cancel()
        isRunning = false
    }

    /*
     * Stop and restart count down timer
     */
    fun restartTimer() {
        stopTimer()
        if (animationAllowed) {
            animation.cancel()
        }
        startTimer(
            timerCount,
            animationAllowed,
            runClockWise,
            _onCountDownTimerStarted,
            _onCountDownTimerStopped,
            _onCountDownTimerRunning
        )
    }

    /*
    * Reset the count down timer
    */
    fun resetTimer() {
        stopTimer()
        if (animationAllowed) {
            animation.cancel()
        }
    }

    /*
    * Return timer is running or not
    */
    fun isRunning(): Boolean {
        return isRunning
    }

    /*
     * To set the description text that under the timer view
     */
    fun setDescriptionText(descriptionText: String?) {
        textViewDescription.apply {
            descriptionText?.let {
                this.text = it
                this.visibility = View.VISIBLE
            }
        }
    }

    /*
     * To change the description text color
     */
    fun setDescriptionTextColor(descriptionTextColor: Int) {
        textViewDescription.setTextColor(descriptionTextColor)
    }

    /*
     * To change the description text size
     */
    fun setDescriptionTextSize(descriptionTextSize: Float) {
        textViewDescription.textSize = descriptionTextSize / resources.displayMetrics.scaledDensity
    }

    /*
     * To change the remaining time text size
     */
    fun setTimeTextSize(timeTextSize: Float) {
        textViewTime.textSize = timeTextSize / resources.displayMetrics.scaledDensity
    }

    /*
     * To change the remaining time text color
     */
    fun setTimeTextColor(timeTextColor: Int) {
        textViewTime.setTextColor(timeTextColor)
    }

    /*
     * To change the color of the progress background
     */
    fun setProgressInnerCircleColor(innerCircleColor: Int) {
        progressBarCircle.background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(
                    context,
                    innerCircleColor
                ), BlendModeCompat.SRC_ATOP
            )
    }

    /*
     * To change the color of the progress drawable
     */
    fun setProgressOuterCircleColor(outerCircleColor: Int) {
        progressBarCircle.progressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(
                    context,
                    outerCircleColor
                ), BlendModeCompat.SRC_ATOP
            )
    }

    override fun onDetachedFromWindow() {
        stopTimer()
        super.onDetachedFromWindow()
    }
}