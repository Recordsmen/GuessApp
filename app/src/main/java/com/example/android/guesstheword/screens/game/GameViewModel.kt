package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)
private var SKIP_BUZZ_PATTERN = longArrayOf(50,200,50,200,50,200)

class GameViewModel:ViewModel() {

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN),
        SKIP_BUZZ(SKIP_BUZZ_PATTERN),
    }


    companion object {
        private const val COUNTDOWN_PANIC_SECONDS = 10L
        const val ONE_SECOND = 1000L
        const val COUNTDOWN_TIME = 60000L
    }
    private lateinit var timer: CountDownTimer

    private lateinit var wordList: MutableList<String>


    private val _word = MutableLiveData<String>()
    val word:LiveData<String>
        get() = _word

    private val _score = MutableLiveData<Int>()
    val score:LiveData<Int>
        get() = _score

    private val _eventGameFinished = MutableLiveData<Boolean>()
    val eventGameFinished:LiveData<Boolean>
        get() = _eventGameFinished

    private val _seconds = MutableLiveData<Long>()
    val seconds:LiveData<Long>
        get() = _seconds

    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    val secondsString = Transformations.map(seconds) { time ->
        DateUtils.formatElapsedTime(time)
    }

    init {
        _eventGameFinished.value = false
        resetList()
        nextWord()
        _score.value = 0
        _seconds.value = 59

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _seconds.value = millisUntilFinished/1000
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
            }
                }

            override fun onFinish() {
                onGameFinishComplete()
                _eventBuzz.value = BuzzType.GAME_OVER
            }
        }
        DateUtils.formatElapsedTime(_seconds.value!!)


        timer.start()
    }
    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }
    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)

    }
    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    fun onSkip() {
        _score.value = (_score.value)?.minus(1)
        nextWord()
        _eventBuzz.value = BuzzType.SKIP_BUZZ
    }

    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        nextWord()
        _eventBuzz.value = BuzzType.CORRECT

    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

    fun onGameFinishComplete(){
        _eventGameFinished.value = true
    }
}