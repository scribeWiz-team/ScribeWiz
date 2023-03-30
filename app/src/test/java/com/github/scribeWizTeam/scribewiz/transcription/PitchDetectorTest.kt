package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.*

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class PitchDetectorTest {

    private var sampleFreq = 44000.0
    private lateinit var detector: PitchDetector

    @Before
    fun prepare_pitch_detector(){
        detector = PitchDetector(sampleFreq)
    }

    @Test
    fun pitch_detector_must_have_positive_samplingFreq() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            PitchDetector(-12.3)
        }
        assertEquals("Sampling frequency must be positive", exception.message)
    }


    @Test
    fun no_signal_gives_no_pitch() {
        assertNull(detector.detect_pitch(emptyList()))
    }

    @Test
    fun silent_signal_gives_no_pitch() {
        val signal = List(2000, { 0.0 })
        assertNull(detector.detect_pitch(signal))
    }

    @Test
    fun correctly_detects_the_pitch_of_a_pure_sine_wave() {
        val signal_freq = 440.0
        val signal = List(2000, { sin(2*PI*signal_freq*it/sampleFreq) })
        assertEquals(signal_freq, detector.detect_pitch(signal))
    }

    @Test
    fun correctly_detects_the_fundamental_frequency_of_a_composed_signal() {
        val signal_freq = 440.0
        val signal = List(2000, { 
                                 sin(2*PI*signal_freq*it/sampleFreq)
                                 +0.6*sin(2*PI*2*signal_freq*it/sampleFreq+1.3)
                                 +0.3*sin(2*PI*3*signal_freq*it/sampleFreq+2)
                                })
        assertEquals(signal_freq, detector.detect_pitch(signal))
    }
}

