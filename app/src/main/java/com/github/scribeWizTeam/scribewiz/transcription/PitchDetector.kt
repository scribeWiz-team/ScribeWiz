package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.*

// for pitch detection:
// https://en.wikipedia.org/wiki/Pitch_detection_algorithm

typealias Signal = FloatArray
typealias Frequency = Double
typealias Energy = Double

interface PitchDetectorInterface {

    val samplingFreq: Frequency

    fun detect_pitch(signal: Signal): Frequency?
}

class PitchDetector(override val samplingFreq: Frequency,
                    val corrThreshold: Double=1.0): PitchDetectorInterface {
    // samplingFreq: the sampling frequency of the microphone
    //               a typical value is 44000.0 Hz
    companion object {
        private const val MIN_FREQ = 50.0 // lowest detectable frequency
        private const val MAX_FREQ = 2000.0 // highest detectable frequency
        private const val TRANSLUCENCY_TH = 1.0 // minimum translucency to detect a frequency
    }

    init {
        if (samplingFreq <= 0){
            throw IllegalArgumentException(
                "sampling frequency of PitchDetector should be positive"
            )
        }
    }

    private fun autocorrelation(signal: Signal, lag: Int): Energy{
        var energy = 0.0
        for (i in 0 until signal.size - lag){
            energy += signal[i]*signal[i+lag]
        }
        return energy.toDouble()
    }

    private fun square_sum(signal: Signal, lag: Int): Energy{
        var energy = 0.0
        for (i in 0 until signal.size - lag){
            val i_square = signal[i]*signal[i]
            val i_lag_square = signal[i+lag]*signal[i+lag]
            energy += i_square + i_lag_square
        }
        return energy.toDouble()
    }

    private fun normal_square_diff(signal: Signal, lag: Int): Energy{
        val r = autocorrelation(signal, lag)
        val m = square_sum(signal, lag)
        val n = 2*r/m
        return max(-1.0, min(1.0, n))
    }

    private fun lag_to_freq(lag: Int): Frequency {
        return samplingFreq / lag
    }

    private fun freq_to_lag(frequency: Frequency): Int {
        return (samplingFreq / frequency).roundToInt()
    }

    override fun detect_pitch(signal: Signal): Frequency? {
        val highLag = freq_to_lag(MIN_FREQ)
        val lowLag = freq_to_lag(MAX_FREQ)
        var bestCorr = -1.0
        var bestLag : Int? = null
        var candidateMax : List<Pair<Int, Energy>> = emptyList()
        for (lag in lowLag .. highLag){
            val currentCorr = normal_square_diff(signal, lag)
            if (currentCorr > 0){
                if (currentCorr > bestCorr){
                    bestCorr = currentCorr
                    bestLag = lag
                }
            } else {
                if (bestLag != null){
                    candidateMax += Pair(bestLag, bestCorr)
                    bestLag = null
                    bestCorr = -1.0
                }
            }
        }
        if (candidateMax.size == 0){
            return null
        }
        val (lagOfMax, maxCorr) = candidateMax.maxBy { it.second }
        val fundamental = candidateMax.find { it.second >= corrThreshold }
        if (fundamental == null){
            return null
        }
        val (fundamentalLag, fundamentalCorr) = fundamental
        val fundFreq = lag_to_freq(fundamentalLag)
        val power = sqrt(autocorrelation(signal, 0))
        val clarity = autocorrelation(signal, fundamentalLag)
        val translucency = power * clarity
        if (translucency < TRANSLUCENCY_TH){
            return null
        }
        return fundFreq
    }
}
