package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.*


const val SILENT_PITCH = -1

data class Note(val pitch: Int, val startTime: Double, val endTime: Double){
    val duration = endTime - startTime
}

class NoteGuesser(val sampleDelay: Double) {
    // Usage:
    // - initialize the NoteGuesser with a sampleDelay
    // - call `add_sample` every sampleDelay seconds with a sampled frequency
    // - when there is no more samples to process, call `end_guessing`
    // - you can retrieve the guessed notes at any time in the `notes` attribute

    var time: Double = 0.0
    var notes: List<Note> = listOf()
    var currentNote: Note = Note(SILENT_PITCH, 0.0, 0.0)

    fun add_sample(pitchFreq: Double?){
        val midiPitch = compute_midi_pitch(pitchFreq)
        if (midiPitch != currentNote.pitch){
            push_current_note()
            currentNote = Note(midiPitch, time, time+sampleDelay)
        }
        currentNote = Note(currentNote.pitch, currentNote.startTime, time+sampleDelay)
        time += sampleDelay
    }

    fun end_guessing(){
        push_current_note()
    }
    
    private fun push_current_note(){
        if (currentNote.duration != 0.0){
            notes += currentNote
            currentNote = Note(SILENT_PITCH, time, time)
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
