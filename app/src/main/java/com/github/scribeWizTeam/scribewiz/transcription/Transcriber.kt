package com.github.scribeWizTeam.scribewiz.transcription


/**
 * Transcribe an audio signal into a human-frendly representation
 *
 * See the documentation of each component to see how to initialize them
 * @param pitch_detector see {@link com.github.scribeWizTeam.scribewiz.transcription.PitchDetector PitchDetector}
 * @param note_guesser see {@link com.github.scribeWizTeam.scribewiz.transcription.NoteGuesser NoteGuesser}
 * @param renderer see {@link com.github.scribeWizTeam.scribewiz.transcription.MusicxmlBuilder MusicxmlBuilder}
 */
class Transcriber(
    val pitch_detector: PitchDetectorInterface,
    val note_guesser: NoteGuesserInterface,
    val renderer: MusicRenderer
) {

    /**
     * call this method with raw audio samples from the microphone
     * every {@link #note_guesser.sampleDelay} seconds
     *
     * @param samples the audio signal to process
     */
    fun process_samples(samples: Signal): Int {
        val pitch = pitch_detector.detect_pitch(samples)
        val note = note_guesser.add_sample(pitch)
        return note
    }

    /**
     * call this method when the recording is finished
     */
    fun end_transcription() {
        note_guesser.end_guessing()
    }

    /**
     * call this method at any time to get a transcription of the music
     */
    fun get_transcription(): String {
        renderer.reset()
        for (note in note_guesser.notes) {
            renderer.add_note(note)
        }
        return renderer.build()
    }

}
