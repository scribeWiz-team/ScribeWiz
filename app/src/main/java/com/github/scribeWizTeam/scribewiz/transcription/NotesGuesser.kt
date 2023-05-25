package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.log2
import kotlin.math.roundToInt


const val SILENT_PITCH = -1

data class MidiNote(val pitch: Int, val startTime: Double, val endTime: Double) {
    val duration = endTime - startTime
}

interface NoteGuesserInterface {

    val sampleDelay: Double
    var notes: MutableList<MidiNote>

    fun addSample(pitchFreq: Double?): Int

    fun endGuessing()
}

/**
 * Guess midi notes from a sequence of pitch frequencies
 *
 * @param sampleDelay the delay between two notes samples
 *              this corresponds to the delay between two calls to {@link #add_sample()}
 *              a typical value is 0.05 s
 *
 * Usage:
 * - initialize the NoteGuesser with a sampleDelay
 * - call {@link #add_sample()} every sampleDelay seconds with a sampled frequency
 * - when there is no more samples to process, call {@link #end_guessing()}
 * - you can retrieve the guessed notes at any time in the {@link #notes} attribute
 */
class NoteGuesser(override val sampleDelay: Double,
                  val silenceMinDuration: Double = 0.0,
                  val moving_window_neighbors: Int = 1): NoteGuesserInterface {
    // the window size is always odd, so that it’s symmetric
    val moving_window_size = 2 * moving_window_neighbors + 1

    override var notes: MutableList<MidiNote> = mutableListOf()

    private var movingWindow: Array<Int> = Array(moving_window_size) { SILENT_PITCH }
    private var windowIndex: Int = 0
    private var enoughData: Boolean = false

    private var time: Double = -sampleDelay * moving_window_neighbors
    private var currentNote: MidiNote = MidiNote(SILENT_PITCH, 0.0, 0.0)

    override fun addSample(pitchFreq: Double?): Int {
        // add the sample to the sliding window
        val midiPitch = computeMidiPitch(pitchFreq)
        movingWindow[windowIndex] = midiPitch
        // get the most frequent pitch in window
        val bestPitch = getMostFrequentPitchInWindow()
        currentNote = if (bestPitch != currentNote.pitch) {
            if (enoughData) {
                pushCurrentNote()
            }
            MidiNote(bestPitch, time, time + sampleDelay)
        } else {
            MidiNote(currentNote.pitch, currentNote.startTime, time + sampleDelay)
        }
        time += sampleDelay
        if (windowIndex >= moving_window_neighbors) {
            enoughData = true
        }
        windowIndex = (windowIndex + 1) % moving_window_size
        return currentNote.pitch
    }

    private fun getMostFrequentPitchInWindow(): Int {
        val best = movingWindow.groupBy { it }
            .mapValues { (_, l) -> l.size }
            .maxBy { it.value }
        if (best.value == moving_window_neighbors) {
            // not enough samples to be representative
            return SILENT_PITCH
        }
        return best.key
    }

    override fun endGuessing() {
        pushCurrentNote()
    }

    private fun pushCurrentNote() {
        if (currentNote.duration != 0.0) {
            if (currentNote.pitch == SILENT_PITCH && currentNote.duration < silenceMinDuration){
                // if the silence is too short, we merge it into the previous note
                val last_note = notes[notes.lastIndex]
                val new_note = MidiNote(last_note.pitch, last_note.startTime, currentNote.endTime)
                notes[notes.lastIndex] = new_note
            } else {
                notes += currentNote
            }
            currentNote = MidiNote(SILENT_PITCH, time, time)
        }
    }

    private fun computeMidiPitch(pitchFreq: Double?): Int {
        // This formula comes from this website
        //  https://newt.phys.unsw.edu.au/jw/notes.html
        if (pitchFreq == null) {
            return SILENT_PITCH
        }
        return (12 * log2(pitchFreq / 440.0) + 69).roundToInt()
    }
}
