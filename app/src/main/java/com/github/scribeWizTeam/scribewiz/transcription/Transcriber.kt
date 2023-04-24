package com.github.scribeWizTeam.scribewiz.transcription


class Transcriber(val micSamplingFreq: Frequency,
                  val noteSamplingDelay: Double,
                  val scoreName: String,
                  val signature: Signature) {
    // micSamplingFreq: the sampling frequency of the microphone
    //                  a typical value is 44000.0 Hz
    // noteSamplingDelay: the delay between two notes samples
    //                    this corresponds to the delay between two calls to
    //                    `process_samples`
    //                    a typical value is 0.05 s
    // scoreName: the name of this musical score
    // signature: the signature of this music score, see MusicxmlBuilder.Signature

    val pitch_detector = PitchDetector(micSamplingFreq)
    val note_guesser = NoteGuesser(noteSamplingDelay)

    fun process_samples(samples: Signal){
        // call this method with raw audio samples from the microphone
        // every `noteSamplingDelay` seconds
        val pitch = pitch_detector.detect_pitch(samples)
        note_guesser.add_sample(pitch)
    }

    fun end_transcription(){
        // call this method when the recording is finished
        note_guesser.end_guessing()
    }

    fun get_transcription(): String {
        // call this method at any time to get a transcription of the music
        // in musicxml format
        val xml_builder = MusicxmlBuilder(scoreName, signature)
        for (note in note_guesser.notes){
            xml_builder.add_note(note)
        }
        return xml_builder.build()
    }

}
