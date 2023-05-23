package com.github.scribeWizTeam.scribewiz.transcription

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before


class TranscriberTest {
    class MockPitchDetector: PitchDetectorInterface {
        override val samplingFreq = 1.0

        override fun detectPitch(signal: Signal): Frequency {
            return 2.0
        }
    }

    class MockNoteGuesser: NoteGuesserInterface {
        override val sampleDelay = 1.0
        override var notes: List<MidiNote> = listOf()

        override fun addSample(pitchFreq: Double?): Int{
            notes += MidiNote(2, 0.0, 1.0)
            return 0
        }

        override fun endGuessing(){
            notes += MidiNote(SILENT_PITCH, 0.0, 1.0)
        }
    }

    class MockMusicRenderer: MusicRenderer {
        private var result: List<String> = listOf()

        override fun addNote(midiNote: MidiNote){
            result += midiNote.pitch.toString()
        }

        override fun build(): String {
            return result.joinToString(separator="|")
        }

        override fun reset(){
            result = listOf()
        }
    }

    private lateinit var transcriber: Transcriber

    @Before
    fun init_transcriber(){
        val pitch_detector = MockPitchDetector()
        val note_guesser = MockNoteGuesser()
        val renderer = MockMusicRenderer()
        transcriber = Transcriber(pitch_detector, note_guesser, renderer)
    }

    @Test
    fun transcriber_calls_processing_blocks_as_expected(){
        val dummy_signal = Signal(2, { 3.0f })
        transcriber.processSamples(dummy_signal)
        assertEquals("2", transcriber.get_transcription())
        transcriber.processSamples(dummy_signal)
        assertEquals("2|2", transcriber.get_transcription())
        transcriber.endTranscription()
        assertEquals("2|2|-1", transcriber.get_transcription())
    }
}

