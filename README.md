# CountDownTimerView
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
	        implementation 'com.github.bsimsek159:CountDownTimerView:0.1.1'
	}

## USAGE

 - Initialize and start the count down timer with startTimer() method and timer state can be listened with this method
 
<img width="363" alt="Screen Shot 2019-07-20 at 12 28 59" src="https://user-images.githubusercontent.com/25201275/61577004-451b5880-aaea-11e9-90f9-c1bdc6ca6a80.png">

 - Some attributes can be setted from the .xml file to which it was added or you can change these attributes with related methods in the component class
 
 ```
	<com.bsimsek.countdowntimerwidget.CountDownTimerView
            android:id="@+id/countDownTimerView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:clockwise="true"
            app:animation="true"
            app:timeTextColor="@color/colorRed"
            app:innerCircleColor="@color/colorRed"
            app:outerCircleColor="@color/grey"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">
    	</com.bsimsek.countdowntimerwidget.CountDownTimerView>
	
    ```


