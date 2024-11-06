package com.bignerdranch.android.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import kotlin.math.roundToInt

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueBtn: Button
    private lateinit var falseBtn: Button
    private lateinit var nextBtn: ImageButton
    private lateinit var cheatBtn: Button
    private lateinit var questionsTextView: TextView
    private lateinit var prevBtn: ImageButton
    private lateinit var textNumHints: TextView
    private lateinit var numOfHintsInXml: TextView

    private val quizViewModel: QuizViewModel by lazy{
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    private var correctAnswers = 0
    private var persentCorrectAnswers: Double = 0.0



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueBtn = findViewById(R.id.true_button)
        falseBtn = findViewById(R.id.false_button)
        nextBtn = findViewById(R.id.next_btn)
        questionsTextView = findViewById(R.id.question_text_view)
        prevBtn = findViewById(R.id.prev_btn)
        cheatBtn = findViewById(R.id.cheat_btn)
        textNumHints = findViewById(R.id.nameNumHints)
        numOfHintsInXml = findViewById(R.id.numHints)



        trueBtn.setOnClickListener { view: View ->
            checkAnswer(true)

            blockBtn(falseBtn)
        }

        falseBtn.setOnClickListener { view: View ->
                checkAnswer(false)

            blockBtn(trueBtn)
        }

        nextBtn.setOnClickListener {
            nextBtnOrText()
            unblockBtn()
            resultQuiz()
        }

        questionsTextView.setOnClickListener {
            nextBtnOrText()
        }

        prevBtn.setOnClickListener{
            prevQuestions()
            unblockBtn()
        }

        cheatBtn.setOnClickListener{ view ->
            if (quizViewModel.numOfHints == 0){
                blockBtn(cheatBtn)
            } else {
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                quizViewModel.numOfHints--
                numOfHintsInXml.setText(" ${quizViewModel.numOfHints}")

                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            }
        }

        updateQuestions()

    }


    private fun unblockBtn() {
        trueBtn.setEnabled(quizViewModel.trueStateBtn)
        falseBtn.setEnabled(quizViewModel.trueStateBtn)
    }

    private fun blockBtn(btn: Button?) {
        if (btn != null) {
            btn.setEnabled(quizViewModel.falseStateBtn)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle){
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun prevQuestions() {
        quizViewModel.moveToBack()
        updateQuestions()
    }

    private fun updateQuestions(){
        val questionsTextResId = quizViewModel.currentQuestionText
        questionsTextView.setText(questionsTextResId)
        quizViewModel.isCheater = false
        numOfHintsInXml.setText(" ${quizViewModel.numOfHints}")
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId: Int =  when {
            quizViewModel.isCheater -> {
                quizViewModel.isCheater = true
                R.string.judgment_toast}
            userAnswer == correctAnswer -> { R.string.correct_toast
                                                correctAnswers++ }
            else -> R.string.incorrect_toast
        }


        makeToast(messageResId)
    }

    private fun makeToast(massage: Int) {
        val toast = Toast.makeText(this,
                massage,
                Toast.LENGTH_SHORT).show()
    }

    private fun nextBtnOrText(){
        quizViewModel.moveToNext()

        updateQuestions()
    }

    private fun resultQuiz() {
        var persents: Int = 0
        persentCorrectAnswers = (correctAnswers.toDouble() / quizViewModel.questionsBankSize)

        if (quizViewModel.currentIndex == 0) {
            persents = (persentCorrectAnswers * 100).roundToInt()
            Toast.makeText(this, "$persents% right answers", Toast.LENGTH_SHORT).show()
            persentCorrectAnswers = 0.0
            quizViewModel.numOfHints = 3
        }
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK){
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
}