package com.bignerdranch.android.geoquiz

import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel: ViewModel() {

    var currentIndex = 0
    var numOfHints = 3
    var isCheater = false
    var trueStateBtn: Boolean = true
    var falseStateBtn: Boolean = false
    private val questionsBank = listOf(
        Questions(R.string.question_australia, true),
        Questions(R.string.question_africa, false),
        Questions(R.string.question_americas, true),
        Questions(R.string.question_asia, true),
        Questions(R.string.question_oceans, true),
        Questions(R.string.question_mideast, false) )

    val questionsBankSize = questionsBank.size

    val currentQuestionAnswer: Boolean
        get() = questionsBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionsBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionsBank.size
    }

    fun moveToBack(){
        if (currentIndex > 0){
            currentIndex = (currentIndex - 1) % questionsBank.size}
        else currentIndex = (questionsBank.size - 1) % questionsBank.size
    }


}