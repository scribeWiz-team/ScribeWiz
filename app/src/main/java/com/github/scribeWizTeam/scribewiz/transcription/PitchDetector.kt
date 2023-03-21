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

    private fun lag_to_freq(lag: Int): Frequency {
        return samplingFreq / lag
    }

    private fun freq_to_lag(frequency: Frequency): Int {
        return (samplingFreq / frequency).roundToInt()
    }

    fun detect_pitch(signal: Signal): Frequency? {
        val highLag = freq_to_lag(50.0)
        val lowLag = freq_to_lag(4000.0)
        val energy = autocorrelation(signal, 0)
        var bestEnergy = 0.0
        var bestLag : Int? = null
        for (lag in highLag downTo lowLag){
            val energy_shifted = autocorrelation(signal, lag)
            if (energy_shifted > bestEnergy){
                bestEnergy = energy_shifted
                bestLag = lag
            }
        }
        if (bestLag == null || bestEnergy < 0.9*energy){
            return null
        }
        return lag_to_freq(bestLag)
    }
}
