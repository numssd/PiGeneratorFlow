package com.example.pigeneratorflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_calculator_pi.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class FragmentCalculatorPI : Fragment() {
    private var counter = 0
    private var running = true
    private var viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calculator_pi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ioScope.launch {
            runTimerPi().collect {
                textViewPi?.text = it
            }
        }
    }

    fun onClickStart() {
        running = true
        ioScope.launch {
            runTimerPi().collect {
                textViewPi?.text = it
            }
        }
    }

    fun onClickPause() {
        running = false
    }

    fun onClickRestart() {
        running = false
        counter = 0
        textViewPi?.text = ""
    }

    private fun runTimerPi(): Flow<String> = flow {
        while (running) {
            delay(100)
            counter++
            val pi = calculateSpigotAlgorithm(counter)
            emit(pi)
        }
    }

    private fun calculateSpigotAlgorithm(n: Int): String {
        val pi = StringBuilder(n)
        val boxes = n * 10 / 3
        val reminders = IntArray(boxes)
        for (i in 0 until boxes) {
            reminders[i] = 2
        }
        var heldDigits = 0
        for (i in 0 until n) {
            var carriedOver = 0
            var sum = 0
            for (j in boxes - 1 downTo 0) {
                reminders[j] *= 10
                sum = reminders[j] + carriedOver
                val quotient = sum / (j * 2 + 1)
                reminders[j] = sum % (j * 2 + 1)
                carriedOver = quotient * j
            }
            reminders[0] = sum % 10
            var q = sum / 10
            when (q) {
                9 -> {
                    heldDigits++
                }
                10 -> {
                    q = 0
                    for (k in 1..heldDigits) {
                        var replaced = pi.substring(i - k, i - k + 1).toInt()
                        if (replaced == 9) {
                            replaced = 0
                        } else {
                            replaced++
                        }
                        pi.deleteCharAt(i - k)
                        pi.insert(i - k, replaced)
                    }
                    heldDigits = 1
                }
                else -> {
                    heldDigits = 1
                }
            }
            pi.append(q)
        }
        if (pi.length >= 2) {
            pi.insert(1, '.')
        }
        return pi.toString()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(COUNTER_KEY, counter)
        outState.putBoolean(RUNNING_KEY, running)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        counter = savedInstanceState?.getInt(COUNTER_KEY) ?: 0
        running = savedInstanceState?.getBoolean(RUNNING_KEY) ?: true
        super.onViewStateRestored(savedInstanceState)
    }

    companion object {
        const val COUNTER_KEY = "counter"
        const val RUNNING_KEY = "running"
    }
}