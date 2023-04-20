package com.github.scribeWizTeam.scribewiz

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import com.github.scribeWizTeam.scribewiz.Util.Editor

@RunWith(AndroidJUnit4::class)
class EditorTest {

    private lateinit var inputMusicXMLFile: File
    private lateinit var outputMusicXMLFile: File

    @Before
    fun setUp() {
        inputMusicXMLFile = File.createTempFile("input_music_file", ".musicxml")
        outputMusicXMLFile = File.createTempFile("output_music_file", ".musicxml")
    }

    @Test
    fun testEditNoteInMusicXML() {
        Editor.editNoteInMusicXML(inputMusicXMLFile, outputMusicXMLFile, 5, "C")

    }

    @After
    fun tearDown() {
        inputMusicXMLFile.delete()
        outputMusicXMLFile.delete()
    }
}