package com.github.scribeWizTeam.scribewiz.transcription


class Transcriber(
    private val pitchDetector: PitchDetectorInterface,
    private val noteGuesser: NoteGuesserInterface,
    private val renderer: MusicRenderer) {
    // See the documentation of each component to see how to initialize them
    // for pitch_detector, see PitchDetector
    // for note_guesser, see NoteGuesser
    // for renderer, see MusicxmlBuilder

    fun processSamples(samples: Signal): Int {
        // call this method with raw audio samples from the microphone
        // every `noteSamplingDelay` seconds
        val pitch = pitchDetector.detectPitch(samples)
        return noteGuesser.addSample(pitch)
    }

    fun endTranscription(){
        // call this method when the recording is finished
        noteGuesser.endGuessing()
    }

    fun get_transcription(): String {
        // call this method at any time to get a transcription of the music
        renderer.reset()
        for (note in noteGuesser.notes){
            renderer.addNote(note)
        }
        return renderer.build()
    }
}

