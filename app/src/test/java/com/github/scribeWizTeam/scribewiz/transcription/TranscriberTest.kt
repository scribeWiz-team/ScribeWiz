package com.github.scribeWizTeam.scribewiz.transcription

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class TranscriberTest {
    class MockPitchDetector: PitchDetectorInterface {
        override val samplingFreq = 1.0

        override fun detectPitch(signal: Signal): Frequency {
            return 2.0
        }
    }

    class MockNoteGuesser: NoteGuesserInterface {
        override val sampleDelay = 1.0
        override var notes: MutableList<MidiNote> = mutableListOf()

        override fun addSample(pitchFreq: Double?): Int{
            notes += MidiNote(2, 0.0, 1.0)
            return 0
        }

        override fun endGuessing(){
            notes += MidiNote(SILENT_PITCH, 0.0, 1.0)
        }
    }

    class MockMusicRenderer: MusicRenderer {
        private var result: MutableList<String> = mutableListOf()

        override fun addNote(midiNote: MidiNote){
            result += midiNote.pitch.toString()
        }

        override fun build(): String {
            return result.joinToString(separator="|")
        }

        override fun reset(){
            result = mutableListOf()
        }
    }

    private lateinit var transcriber: Transcriber

    @Before
    fun init_transcriber(){
        val pitchDetector = MockPitchDetector()
        val noteGuesser = MockNoteGuesser()
        val renderer = MockMusicRenderer()
        transcriber = Transcriber(pitchDetector, noteGuesser, renderer)
    }

    @Test
    fun transcriber_calls_processing_blocks_as_expected(){
        val dummySignal = Signal(2) { 3.0f }
        transcriber.processSamples(dummySignal)
        assertEquals("2", transcriber.get_transcription())
        transcriber.processSamples(dummySignal)
        assertEquals("2|2", transcriber.get_transcription())
        transcriber.endTranscription()
        assertEquals("2|2|-1", transcriber.get_transcription())
    }
}

