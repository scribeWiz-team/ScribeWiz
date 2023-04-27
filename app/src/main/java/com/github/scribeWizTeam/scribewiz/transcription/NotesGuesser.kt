package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.*


const val SILENT_PITCH = -1

data class MidiNote(val pitch: Int, val startTime: Double, val endTime: Double){
    val duration = endTime - startTime
}

interface NoteGuesserInterface {

    val sampleDelay: Double
    var notes: List<MidiNote>

    fun add_sample(pitchFreq: Double?)

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

    override var notes: List<MidiNote> = listOf()

    private var time: Double = 0.0
    private var currentNote: MidiNote = MidiNote(SILENT_PITCH, 0.0, 0.0)

    override fun add_sample(pitchFreq: Double?){
        val midiPitch = compute_midi_pitch(pitchFreq)
        if (midiPitch != currentNote.pitch){
            push_current_note()
            currentNote = MidiNote(midiPitch, time, time+sampleDelay)
        }
        currentNote = MidiNote(currentNote.pitch, currentNote.startTime, time+sampleDelay)
        time += sampleDelay
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
