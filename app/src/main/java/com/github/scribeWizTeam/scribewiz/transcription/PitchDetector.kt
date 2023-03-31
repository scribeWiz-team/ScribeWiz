package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.*

// for pitch detection:
// https://en.wikipedia.org/wiki/Pitch_detection_algorithm

typealias Signal = List<Double>
typealias Frequency = Double
typealias Energy = Double

class PitchDetector(val samplingFreq: Frequency){

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
        return energy
    }

    private fun square_sum(signal: Signal, lag: Int): Energy{
        var energy = 0.0
        for (i in 0 until signal.size - lag){
            energy += signal[i].pow(2)+signal[i+lag].pow(2)
        }
        return energy
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

    fun detect_pitch(signal: Signal): Frequency? {
        val highLag = freq_to_lag(50.0)
        val lowLag = freq_to_lag(4000.0)
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
        val corrThreshold = 1.0
        val fundamental = candidateMax.find { it.second >= corrThreshold }
        if (fundamental == null){
            return null
        }
        val (fundamentalLag, fundamentalCorr) = fundamental
        val fundFreq = lag_to_freq(fundamentalLag)
        return fundFreq
    }
}