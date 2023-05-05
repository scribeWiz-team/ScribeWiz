package com.github.scribeWizTeam.scribewiz.transcription


class Transcriber(val pitch_detector: PitchDetectorInterface,
                  val note_guesser: NoteGuesserInterface,
                  val renderer: MusicRenderer) {
    // See the documentation of each component to see how to initialize them
    // for pitch_detector, see PitchDetector
    // for note_guesser, see NoteGuesser
    // for renderer, see MusicxmlBuilder

    fun process_samples(samples: Signal): Int {
        // call this method with raw audio samples from the microphone
        // every `noteSamplingDelay` seconds
        val pitch = pitch_detector.detect_pitch(samples)
        val note = note_guesser.add_sample(pitch)
        return note
    }

    fun end_transcription(){
        // call this method when the recording is finished
        note_guesser.end_guessing()
    }

    fun get_transcription(): String {
        // call this method at any time to get a transcription of the music
        renderer.reset()
        for (note in note_guesser.notes){
            renderer.add_note(note)
        }
        return renderer.build()
    }

}
