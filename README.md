# CountDownTimerWidget
Count Down timer UI component with progress bar

<img width="500" alt="Screenshot_1563611909" src="https://user-images.githubusercontent.com/25201275/61576994-32a11f00-aaea-11e9-8033-f3465c6f3638.png">

Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.bsimsek159:CountDownTimerView:0.1.7'
	}

## USAGE

 - Initialize and start the count down timer with startTimer() method and timer state can be listened with this method

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

 - Some attributes can be setted from the .xml file to which it was added or you can change these attributes with related methods in the component class
 
 ```
	<com.bsimsek.countdowntimerwidget.CountDownTimerView
            android:id="@+id/countDownTimerView"
            android:layout_width="100dp"
            android:layout_height="100dp"
            app:clockwise="true"
            app:animation="true"
	    app:timeTextSize="@dimen/time_font_xl"
            app:timeTextColor="@color/colorRed"
            app:innerCircleColor="@color/colorRed"
            app:outerCircleColor="@color/colorGrey"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">
    	</com.bsimsek.countdowntimerwidget.CountDownTimerView>


