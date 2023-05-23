package com.github.scribeWizTeam.scribewiz.transcription

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class PitchDetectorTest {

    private var sampleFreq = 44000.0
    private lateinit var detector: PitchDetectorInterface

    @Before
    fun prepare_pitch_detector(){
        detector = PitchDetector(sampleFreq)
    }

    @Test
    fun pitch_detector_must_have_positive_samplingFreq() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            PitchDetector(-12.3)
        }
        assertEquals("sampling frequency of PitchDetector should be positive", exception.message)
    }


    @Test
    fun no_signal_gives_no_pitch() {
        assertNull(detector.detectPitch(Signal(0)))
    }

    @Test
    fun silent_signal_gives_no_pitch() {
        val signal = Signal(2000) { 0.0f }
        assertNull(detector.detectPitch(signal))
    }

    @Test
    fun correctly_detects_the_pitch_of_a_pure_sine_wave() {
        val signalFreq = 440.0
        val signal = Signal(2000) { (1000 * sin(2 * PI * signalFreq * it / sampleFreq)).toFloat() }
        assertEquals(signalFreq, detector.detectPitch(signal))
    }

    @Test
    fun correctly_detects_the_fundamental_frequency_of_a_composed_signal() {
        val signalFreq = 440.0
        val signal = Signal(2000) {
            (1000 * (sin(2 * PI * signalFreq * it / sampleFreq)
                    + 0.6 * sin(2 * PI * 2 * signalFreq * it / sampleFreq + 1.3)
                    + 0.3 * sin(2 * PI * 3 * signalFreq * it / sampleFreq + 2))).toFloat()
        }
        assertEquals(signalFreq, detector.detectPitch(signal))
    }
}

