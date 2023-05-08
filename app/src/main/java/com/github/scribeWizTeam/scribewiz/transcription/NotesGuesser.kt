package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.*


const val SILENT_PITCH = -1

data class MidiNote(val pitch: Int, val startTime: Double, val endTime: Double){
    val duration = endTime - startTime
}

interface NoteGuesserInterface {

    val sampleDelay: Double
    var notes: List<MidiNote>

    fun add_sample(pitchFreq: Double?): Int

    fun end_guessing()
}

class NoteGuesser(override val sampleDelay: Double): NoteGuesserInterface {
    // sampleDelay: the delay between two notes samples
    //              this corresponds to the delay between two calls to `process_samples`
    //              a typical value is 0.05 s
    //
    // Usage:
    // - initialize the NoteGuesser with a sampleDelay
    // - call `add_sample` every sampleDelay seconds with a sampled frequency
    // - when there is no more samples to process, call `end_guessing`
    // - you can retrieve the guessed notes at any time in the `notes` attribute
    companion object {
        private const val MOVING_WINDOW_NEIGHBORS = 1
        // the window size is always odd, so that it’s symmetric
        private const val MOVING_WINDOW_SIZE = 2*MOVING_WINDOW_NEIGHBORS + 1
    }

    override var notes: List<MidiNote> = listOf()

    private var movingWindow: Array<Int> = Array(MOVING_WINDOW_SIZE, { SILENT_PITCH })
    private var windowIndex: Int = 0
    private var enoughData: Boolean = false

    private var time: Double = -sampleDelay*MOVING_WINDOW_NEIGHBORS
    private var currentNote: MidiNote = MidiNote(SILENT_PITCH, 0.0, 0.0)

    override fun add_sample(pitchFreq: Double?): Int {
        // add the sample to the sliding window
        val midiPitch = compute_midi_pitch(pitchFreq)
        movingWindow[windowIndex] = midiPitch
        // get the most frequent pitch in window
        val bestPitch = get_most_frequent_pitch_in_window()
        if (bestPitch != currentNote.pitch){
            if (enoughData){
                push_current_note()
            }
            currentNote = MidiNote(bestPitch, time, time+sampleDelay)
        } else {
            currentNote = MidiNote(currentNote.pitch, currentNote.startTime, time+sampleDelay)
        }
        time += sampleDelay
        if (windowIndex >= MOVING_WINDOW_NEIGHBORS){
            enoughData = true
        }
        windowIndex = (windowIndex + 1) % MOVING_WINDOW_SIZE
        return currentNote.pitch
    }

    private fun get_most_frequent_pitch_in_window(): Int {
        val best = movingWindow.groupBy({it})
                               .mapValues({ (_, l) -> l.size})
                               .maxBy({ it.value })
        if (best.value == MOVING_WINDOW_NEIGHBORS){
            // not enough samples to be representative
            return SILENT_PITCH
        }
        return best.key
    }

    override fun end_guessing(){
        push_current_note()
    }
    
    private fun push_current_note(){
        if (currentNote.duration != 0.0){
            notes += currentNote
            currentNote = MidiNote(SILENT_PITCH, time, time)
        }
    }

    private fun compute_midi_pitch(pitchFreq: Double?): Int {
        // This formula comes from this website
        //  https://newt.phys.unsw.edu.au/jw/notes.html
        if (pitchFreq == null){
            return SILENT_PITCH
        }
        return (12*log2(pitchFreq/440.0)+69).roundToInt()
    }
}
