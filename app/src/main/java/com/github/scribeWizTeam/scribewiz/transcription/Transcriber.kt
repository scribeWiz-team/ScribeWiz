package com.github.scribeWizTeam.scribewiz.transcription


/**
 * Transcribe an audio signal into a human-friendly representation
 *
 * See the documentation of each component to see how to initialize them
 * @param pitchDetector see {@link com.github.scribeWizTeam.scribewiz.transcription.PitchDetector PitchDetector}
 * @param noteGuesser see {@link com.github.scribeWizTeam.scribewiz.transcription.NoteGuesser NoteGuesser}
 * @param renderer see {@link com.github.scribeWizTeam.scribewiz.transcription.MusicxmlBuilder MusicxmlBuilder}
 */
class Transcriber(
    private val pitchDetector: PitchDetectorInterface,
    private val noteGuesser: NoteGuesserInterface,
    private val renderer: MusicRenderer) {

    /**
     * call this method with raw audio samples from the microphone
     * every {@link #note_guesser.sampleDelay} seconds
     *
     * @param samples the audio signal to process
     */
    fun processSamples(samples: Signal): Int {
        // call this method with raw audio samples from the microphone
        // every `noteSamplingDelay` seconds
        val pitch = pitchDetector.detectPitch(samples)
        return noteGuesser.addSample(pitch)
    }

    /**
     * call this method when the recording is finished
     */
    fun endTranscription(){
        // call this method when the recording is finished
        noteGuesser.endGuessing()
    }

    /**
     * call this method at any time to get a transcription of the music
     */
    fun getTranscription(): String {
        renderer.reset()
        for (note in noteGuesser.notes){
            renderer.addNote(note)
        }
        return renderer.build()
    }
}

