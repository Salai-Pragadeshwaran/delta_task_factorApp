package com.example.factor

import android.content.Context
import android.graphics.Color
import android.graphics.Color.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Vibrator as Vibrator1


// TO DO
// crash when options are clicked initially - checked
// avoid increment in the score when button is clicked again for the same question - checked
// make sure data is not lost when clicking back button and reopening app - checked
// make sure data is not lost when changing orientation - checked
// change colour only for 1 second - checked
// crash when no num is submitted - checked
// Understand shared preferences and the vibrator code - checked
// change orientation - timer resets

var num = 0
var a: Int = 0
var b: Int = 0
var c: Int = 0
var retain = 0
var score : Int = 0
var highScore = 0
var textString : String = ""
var answeredQuestion = true
var timerRunning = false
var isTimerViewVisible = true



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ConstraintLayout>(R.id.mainLayout).setBackgroundColor(parseColor("#B3E5FC"))

        val colorTimer = object : CountDownTimer(1000, 500) {

            override fun onTick(millisUntilFinished: Long) {
                //do nothing
            }

            override fun onFinish() {
                findViewById<ConstraintLayout>(R.id.mainLayout).setBackgroundColor(parseColor("#B3E5FC"))
            }
        }


        fun timeUpToast(){
            Toast. makeText( this , "Time up ! The correct answer is $c", Toast. LENGTH_SHORT). show()
        }


        fun timerViewToggle(){
            if (isTimerViewVisible){
                timerView.visibility = View.INVISIBLE
                isTimerViewVisible = false
            }
            else{
                timerView.visibility = View.VISIBLE
                isTimerViewVisible = true
            }
        }

        val timer = object : CountDownTimer(21000, 250) {

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 4000) {
                    timerView.setTextColor(rgb(239, 83, 80))
                    timerViewToggle()
                } else {
                    timerView.setTextColor(BLACK)
                }
                textString = (millisUntilFinished / 1000).toString()
                timerView.text = textString
                timerProgress.progress = (5*(millisUntilFinished / 1000)).toInt()
            }

            override fun onFinish() {
                timerView.visibility = View.VISIBLE
                timerRunning = false
                textString = "0"
                timerView.text = textString
                timerView.setTextColor(BLACK)
                timeUpToast()
                wrongAnswer()
                timerProgress.progress = 100
                colorTimer.start()
                textString = "Current Score: $score"
                scoreView.text = textString
            }
        }


        // To retain the same stuff even when orientation is changed
        if (retain != 0) {
            setOptionsText()
            textString = "Current Score: $score"
            scoreView.text = textString
            textString = "Which number is a factor of $num"
            questionView.text = textString
        }


        val mypreference = MyPreference(this)
        var highScoreCount = mypreference.getHSCount()
        highScore = highScoreCount
        mypreference.setHSCount(highScoreCount)
        textString = "High Score: $highScoreCount"
        highScoreView.text = textString


        fab.setOnClickListener{
            highScoreCount = mypreference.getHSCount()
            score = 0
            textString = "Current Score: $score"
            scoreView.text = textString
            highScoreCount = 0
            highScore = highScoreCount
            mypreference.setHSCount(highScoreCount)
            textString = "High Score: $highScoreCount"
            highScoreView.text = textString
        }

        submitNumber.setOnClickListener {

            if((practiseNumber.text.toString()!="")&&(!timerRunning)) {  //to allow the submission of number only when a number is entered
                num = practiseNumber.text.toString().toDouble().toInt()
                practiseNumber.text = null
                answeredQuestion = false
                if (isNotPrime(num)) {
                    setOptions()
                    timer.start()
                    timerRunning = true
                }
                else
                    Toast.makeText(this, "prime number / Invalid Entry", Toast.LENGTH_SHORT).show()
            }
        }


        fun optionClicked (numOnOption: Int){
            if ((num != 0)&&(timerView.text.toString() != "0")&&(!answeredQuestion)) {
                checkAnswer(numOnOption)
                colorTimer.start()
                timer.cancel()
                timerRunning = false
                answeredQuestion = true
            }
        }

        option1.setOnClickListener {
            if ((num != 0)&&(timerView.text.toString() != "0")&&(!answeredQuestion))
                optionClicked(option1.text.toString().toInt())
        }

        option2.setOnClickListener {
            if ((num != 0)&&(timerView.text.toString() != "0")&&(!answeredQuestion))
                optionClicked(option2.text.toString().toInt())
        }

        option3.setOnClickListener {
            if ((num != 0)&&(timerView.text.toString() != "0")&&(!answeredQuestion))
                optionClicked(option3.text.toString().toInt())
        }
    }


    private fun setOptions() {  //Function to set the options based on the input number
        // better algo for setting c
        var range : Int
        if (num > 15)
            range = num/2
        else
            range = num -1

        while( (a==0) || (num%a==0) ){
            a = (2..(range)).random()
        }

        while( (b==0) || (num%b==0) || (b==a) ){
            b = (2..(range)).random()
        }

        while( (c==0) || (num%c!=0) ){
            c = (2..(num/2)).random() // This is the factor of num
        }

        retain = a
        setOptionsText()
        textString = "Which number is a factor of $num"
        questionView.text = textString
    }


    private fun setOptionsText(){
        val ar = arrayOf(a, b, c)
        val i = (1..3).random()
        option1.text =  ar[i%3].toString()
        option2.text =  ar[(i+1)%3].toString()
        option3.text =  ar[(i+2)%3].toString()
    }


    private fun checkAnswer (selectedOption : Int){

        if(num%selectedOption==0) {
            Toast.makeText(this, "Correct answer !", Toast.LENGTH_SHORT).show()
            correctAnswerSound()
            findViewById<ConstraintLayout>(R.id.mainLayout).setBackgroundColor(rgb(56,142,60))
            score++
            setHighScore()
        }

        else{
            Toast. makeText( this , "Wrong answer, The correct answer is $c", Toast. LENGTH_SHORT). show()
            wrongAnswer()
        }

        textString = "Current Score: $score"
        scoreView.text = textString
        a = 0
        b = 0
        c = 0
    }


    private fun wrongAnswer(){
        wrongAnswerSound()
        vibratePhone()
        findViewById<ConstraintLayout>(R.id.mainLayout).setBackgroundColor(parseColor("#D32F2F"))
        setHighScore()
        score = 0
    }


    private fun setHighScore() {

        if ( score > highScore){
            Toast. makeText( this , "New High Score !!!", Toast. LENGTH_SHORT). show()
            highScore = score
            val mypreference = MyPreference(this)
            var highScoreCount = highScore
            mypreference.setHSCount(highScoreCount)
            textString = "High Score: $highScore"
            highScoreView.text = textString
        }

    }


    private fun isNotPrime(n : Int): Boolean{
        if (n==4)
            return false
        for(i in  2..(n/2)){
            if(n%i==0)
                return true
        }
        return false
    }


    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator1
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
            } else {
                vibrator.vibrate(500) // Vibrate method for below API Level 26
            }
    }}


    private fun correctAnswerSound(){
        var mediaPlayer= MediaPlayer.create(this  , R.raw.correct_answer)
        mediaPlayer.start()

    }


    private fun wrongAnswerSound(){
        var mediaPlayer = MediaPlayer.create(this , R.raw.wrong_answer)
       mediaPlayer.start()

    }

}
