package com.github.scribeWizTeam.scribewiz.transcription

import org.junit.Test

import org.junit.Assert.*


class NoteGuesserTest {
    @Test
    fun sequence_of_pure_silence_is_guessed_properly() {
        val samples: List<Double?> = listOf(
            null,
            null,
            null,
            null
        )

        val guesser = NoteGuesser(0.2)
        for (sample in samples){
            guesser.add_sample(sample)
        }
        guesser.end_guessing()

        assertEquals(1, guesser.notes.size)
        val silence = guesser.notes.get(0)
        assertEquals(SILENT_PITCH, silence.pitch)
        assertEquals(0.8, silence.duration, 0.001)
    }

    @Test
    fun sequence_of_3_notes_is_guessed_properly() {
        val samples: List<Double?> = listOf(
            // 60 | C4 | 261 Hz
            261.0,
            262.0,
            261.0,
            260.0,
            // 55 | G3 | 196 Hz
            196.0,
            197.0,
            196.5,
            195.2,
            // 64 | E4 | 329 Hz
            329.0,
            330.0,
            330.3,
            328.4,
            327.7,
            331.9,
            329.4,
            325.9,
        )
        val expected: List<Pair<Int, Double>> = listOf(
            Pair(60, 2.0),
            Pair(55, 2.0),
            Pair(64, 4.0)
        )

        val guesser = NoteGuesser(0.5)
        for (sample in samples){
            guesser.add_sample(sample)
        }
        guesser.end_guessing()

        assertEquals(expected.size, guesser.notes.size)
        for ((i, exp) in expected.withIndex()){
            val (midiPitch, duration) = exp
            val note = guesser.notes.get(i)
            assertEquals(midiPitch, note.pitch)
            assertEquals(duration, note.duration, 0.001)
        }
            
    }
}
