package com.github.scribeWizTeam.scribewiz.Util

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Editor {

    companion object {
        // Function to edit a note at a specific location in a MusicXML file.
        fun editNoteInMusicXML(outputFile: File, inputFile: File, noteLocation: Int, newNote: String) {
            // Initialize XmlPullParser for parsing the input MusicXML file.
            val parserFactory = XmlPullParserFactory.newInstance()
            val parser = parserFactory.newPullParser()
            val inputStream = FileInputStream(inputFile)
            parser.setInput(inputStream, null)

            // Initialize XmlSerializer for writing the modified content to the output MusicXML file.
            val xmlSerializer = Xml.newSerializer()
            val outputStream = FileOutputStream(outputFile)
            xmlSerializer.setOutput(outputStream, "UTF-8")
            xmlSerializer.startDocument(null, true)

            // Variable to keep track of the current note location in the input MusicXML file.
            var eventType = parser.eventType
            var currentNoteLocation = 0

            // Loop through the input file's XML content until reaching the end of the document, and write the note at the desired location.
            loopXMLDocument(eventType, parser, xmlSerializer, currentNoteLocation, noteLocation, newNote)

            // End the document and close the input and output file streams.
            xmlSerializer.endDocument()
            inputStream.close()
            outputStream.close()
        }


        // Helper Function to loop through the input file's XML content until reaching the end of the document and writes the note at the desired location.
        private fun loopXMLDocument(eventType: Int, parser: XmlPullParser, xmlSerializer: XmlSerializer, currentNoteLocation: Int, noteLocation: Int, newNote: String) {
            var eventType1 = eventType
            var currentNoteLocation1 = currentNoteLocation
            while (eventType1 != XmlPullParser.END_DOCUMENT) {
                when (eventType1) {
                    // Start of an XML tag.
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name

                        // Write the start tag to the XmlSerializer.
                        xmlSerializer.startTag(null, tagName)

                        // Copy all attributes from the current XML tag to the XmlSerializer.
                        copyTagToSerializer(parser, xmlSerializer)

                        // If the current tag is a "note" tag, increment the currentNoteLocation.
                        if (tagName == "note") {
                            currentNoteLocation1++
                        }
                        // If the current tag is a "step" tag and the current note is at the desired location:
                        // write the new note pitch to the XmlSerializer and skip the original note text.
                        if (tagName == "step" && currentNoteLocation1 == noteLocation) {
                            xmlSerializer.text(newNote)
                            eventType1 = parser.next() // Skip the original note text
                        }
                    }
                    // End of an XML tag.
                    XmlPullParser.END_TAG -> {
                        // Write the end tag to the XmlSerializer.
                        xmlSerializer.endTag(null, parser.name)
                    }

                    // Text content between XML tags.
                    XmlPullParser.TEXT -> {
                        //Copy the text content to the XmlSerializer.
                        xmlSerializer.text(parser.text)
                    }
                }
                //Move to the next event in the input MusicXML file.
                eventType1 = parser.next()
            }
        }

        // Helper Function to copy all attributes from an XML tag to an XmlSerializer.
        private fun copyTagToSerializer(parser: XmlPullParser, xmlSerializer: XmlSerializer) {
            for (i in 0 until parser.attributeCount) {
                xmlSerializer.attribute(
                        null,
                        parser.getAttributeName(i),
                        parser.getAttributeValue(i)
                )
            }
        }

        // Function to help convert from Midi Ticks to MusicXML noteLocaction
        fun convertTicksToNoteLocation(ticks: Int): Int {
            return ticks / 24
        }

    }





}