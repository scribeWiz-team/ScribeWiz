package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

// for pitch detection:
// https://en.wikipedia.org/wiki/Pitch_detection_algorithm

typealias Signal = FloatArray
typealias Frequency = Double
typealias Energy = Double

interface PitchDetectorInterface {

    val samplingFreq: Frequency

    fun detectPitch(signal: Signal): Frequency?
}

class PitchDetector(
    override val samplingFreq: Frequency,
    private val corrThreshold: Double = 1.0
) : PitchDetectorInterface {
    // samplingFreq: the sampling frequency of the microphone
    //               a typical value is 44000.0 Hz
    companion object {
        private const val MIN_FREQ = 50.0 // lowest detectable frequency
        private const val MAX_FREQ = 2000.0 // highest detectable frequency
        private const val TRANSLUCENCY_TH = 1.0 // minimum translucency to detect a frequency
    }

    init {
        if (samplingFreq <= 0) {
            throw IllegalArgumentException(
                "sampling frequency of PitchDetector should be positive"
            )
        }
    }

    private fun autoCorrelation(signal: Signal, lag: Int): Energy {
        var energy = 0.0
        for (i in 0 until signal.size - lag) {
            energy += signal[i] * signal[i + lag]
        }
        return energy
    }

    private fun squareSum(signal: Signal, lag: Int): Energy {
        var energy = 0.0
        for (i in 0 until signal.size - lag) {
            val iSquare = signal[i] * signal[i]
            val iLagSquare = signal[i + lag] * signal[i + lag]
            energy += iSquare + iLagSquare
        }
        return energy
    }

    private fun normalSquareDiff(signal: Signal, lag: Int): Energy {
        val r = autoCorrelation(signal, lag)
        val m = squareSum(signal, lag)
        val n = 2 * r / m
        return max(-1.0, min(1.0, n))
    }

    private fun lagToFreq(lag: Int): Frequency {
        return samplingFreq / lag
    }

    private fun freqToLag(frequency: Frequency): Int {
        return (samplingFreq / frequency).roundToInt()
    }

    override fun detectPitch(signal: Signal): Frequency? {
        val highLag = freqToLag(MIN_FREQ)
        val lowLag = freqToLag(MAX_FREQ)
        var bestCorr = -1.0
        var bestLag: Int? = null
        val candidateMax: MutableList<Pair<Int, Energy>> = mutableListOf()
        for (lag in lowLag..highLag) {
            val currentCorr = normalSquareDiff(signal, lag)
            if (currentCorr > 0) {
                if (currentCorr > bestCorr) {
                    bestCorr = currentCorr
                    bestLag = lag
                }
            } else {
                if (bestLag != null) {
                    candidateMax += Pair(bestLag, bestCorr)
                    bestLag = null
                    bestCorr = -1.0
                }
            }
        }
        if (candidateMax.size == 0) {
            return null
        }
        val fundamental = candidateMax.find { it.second >= corrThreshold } ?: return null
        val (fundamentalLag, _) = fundamental
        val fundFreq = lagToFreq(fundamentalLag)
        val power = sqrt(autoCorrelation(signal, 0))
        val clarity = autoCorrelation(signal, fundamentalLag)
        val translucency = power * clarity
        if (translucency < TRANSLUCENCY_TH) {
            return null
        }
        return fundFreq
    }
}
