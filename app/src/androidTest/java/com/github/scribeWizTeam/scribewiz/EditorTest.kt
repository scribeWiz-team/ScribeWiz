package com.github.scribeWizTeam.scribewiz

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import com.github.scribeWizTeam.scribewiz.Util.Editor
import org.junit.Assert
import java.io.PrintWriter

@RunWith(AndroidJUnit4::class)
class EditorTest {

    private lateinit var inputMusicXMLFile: File
    private lateinit var outputMusicXMLFile: File
    private lateinit var expectedOutputMusicXMLFile: File

    private fun writeToFile(file: File, content: String) {
        PrintWriter(file).use { out -> out.print(content) }
    }

    @Before
    fun setUp() {
        inputMusicXMLFile = File.createTempFile("input_music_file", ".musicxml")
        outputMusicXMLFile = File.createTempFile("output_music_file", ".musicxml")
        expectedOutputMusicXMLFile = File.createTempFile("expected_output_music_file", ".musicxml")
    }

    @Test
    fun testEditNoteInMusicXML_EmptyInput() {
        val inputXMLContent = ""
        writeToFile(inputMusicXMLFile, inputXMLContent)

        Editor.editNoteInMusicXML(inputMusicXMLFile, outputMusicXMLFile, 5, "G")
        Assert.assertEquals("", outputMusicXMLFile.readText())
    }

    @Test
    fun testEditNoteInMusicXML_NoteNotFound() {
        val inputXMLContent = """
        <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
        <score>
            <note>
                <pitch>
                    <step>A</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>B</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>C</step>
                </pitch>
            </note>
        </score>
    """.trimIndent()
        writeToFile(inputMusicXMLFile, inputXMLContent)

        Editor.editNoteInMusicXML(outputMusicXMLFile, inputMusicXMLFile, 5, "G")

        val expectedNotes = getNotes(inputXMLContent)
        val actualNotes = getNotes(outputMusicXMLFile.readText())

        Assert.assertEquals(expectedNotes, actualNotes)
    }

    @Test
    fun testEditNoteInMusicXML_ValidInput() {
        val inputXMLContent = """
        <?xml version="1.0"?>
        <score>
            <note>
                <pitch>
                    <step>A</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>B</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>C</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>D</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>E</step>
                </pitch>
            </note>
        </score>
    """.trimIndent()
        writeToFile(inputMusicXMLFile, inputXMLContent)

        val expectedOutputXMLContent = """
        <?xml version="1.0"?>
        <score>
            <note>
                <pitch>
                    <step>A</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>B</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>C</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>D</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>G</step>
                </pitch>
            </note>
        </score>
    """.trimIndent()

        Editor.editNoteInMusicXML(inputMusicXMLFile, outputMusicXMLFile, 5, "G")

        val expectedNotes = getNotes(expectedOutputXMLContent)
        val actualNotes = getNotes(outputMusicXMLFile.readText())

        Assert.assertEquals(expectedNotes, actualNotes)
    }

    private fun getNotes(xmlContent: String): List<String> {
        val notePattern = "<note>.*?</note>".toRegex()
        return notePattern.findAll(xmlContent).map { it.value }.toList()
    }


    @After
    fun tearDown() {
        inputMusicXMLFile.delete()
        outputMusicXMLFile.delete()
        expectedOutputMusicXMLFile.delete()
    }
}