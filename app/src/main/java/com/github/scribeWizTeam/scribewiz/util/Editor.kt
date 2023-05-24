package com.github.scribeWizTeam.scribewiz.util

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Editor {

    companion object {

        /**
         * Function to edit a note at a specific location in a MusicXML file.
         *
         * @param outputFile   The output file to store the modified MusicXML.
         * @param inputFile    The input file containing the original MusicXML.
         * @param noteLocation The location of the note to be edited.
         * @param newNote      The new note to replace the original note.
         * @throws IOException              If an I/O error occurs.
         * @throws XmlPullParserException   If an error occurs during XML parsing.
         */
        fun editNoteInMusicXML(
            outputFile: File,
            inputFile: File,
            noteLocation: Int,
            newNote: String
        ) {
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
            val eventType = parser.eventType
            val currentNoteLocation = 0

            // Loop through the input file's XML content until reaching the end of the document, and write the note at the desired location.
            loopXMLDocument(
                eventType,
                parser,
                xmlSerializer,
                currentNoteLocation,
                noteLocation,
                newNote
            )

            // End the document and close the input and output file streams.
            xmlSerializer.endDocument()
            inputStream.close()
            outputStream.close()
        }


        // Helper Function to loop through the input file's XML content until reaching the end of the document and writes the note at the desired location.
        private fun loopXMLDocument(
            eventType: Int,
            parser: XmlPullParser,
            xmlSerializer: XmlSerializer,
            currentNoteLocation: Int,
            noteLocation: Int,
            newNote: String
        ) {
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

        /**
         * Function to help convert from Midi Ticks to MusicXML note location.
         *
         * @param inputFile the input MusicXML file
         * @param ticks the midi ticks
         *
         * @return the corresponding note location in MusicXML
         */
        fun convertTicksToNoteLocation(inputFile: File, ticks: Int): Int {
            // Create a new instance of XmlPullParserFactory and XmlPullParser
            val parserFactory = XmlPullParserFactory.newInstance()
            val parser = parserFactory.newPullParser()

            // Create an input stream for the input file and set it as the input for the parser
            val inputStream = FileInputStream(inputFile)
            parser.setInput(inputStream, null)

            // Initialize variables
            var eventType = parser.eventType
            var currentTicks = 1
            var noteLocation = 0
            var divisions = 0
            val currentDivisionTicks = 480

            // Iterate through the XML file
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        // If the current tag is "note", increment the note location
                        val tagName = parser.name
                        if (tagName == "note") {
                            if (currentTicks >= ticks) {
                                return noteLocation
                            }
                            noteLocation++
                        }
                        // If the current tag is "divisions", get the divisions value
                        else if (tagName == "divisions") {
                            eventType = parser.next()
                            divisions = parser.text.toInt()
                        }
                        // If the current tag is "duration", get the duration and calculate the current division ticks
                        else if (tagName == "duration") {
                            eventType = parser.next()
                            val duration = parser.text.toInt()

                        }
                    }
                    XmlPullParser.END_TAG -> {
                        // If the current tag is "note", update the current ticks
                        val tagName = parser.name
                        if (tagName == "note") {
                            currentTicks += currentDivisionTicks
                        }
                    }
                }
                eventType = parser.next()
            }

            // Close the input stream and return the note location
            inputStream.close()
            return noteLocation
        }

        /**
         * This function counts the number of notes (including rests, half notes, quarter notes)
         * that are within the first quarter partitions in a MusicXML file.
         *
         * @param inputFile The input MusicXML file.
         * @param quarterNotes The number of quarter notes to include in the count.
         * @return The number of notes that start within the specified duration.
         */
        fun getNoteCountWithinQuarterNotes(inputFile: File, quarterNotes: Int): Int {
            // Create a new instance of XmlPullParserFactory and XmlPullParser
            val parserFactory = XmlPullParserFactory.newInstance()
            val parser = parserFactory.newPullParser()

            // Open the input file and set it as the input for the parser
            val inputStream = FileInputStream(inputFile)
            parser.setInput(inputStream, null)

            // Initialize variables
            var eventType = parser.eventType
            var noteCount = 0
            var divisions = 1 // Default divisions to 1 if not set in the file
            var totalQuarterNotes = 0.0 // Cumulative total of quarter notes encountered so far
            val ticksPerQuarterNote = 480 // Default ticks per quarter note

            // Parse through the XML file
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    val tagName = parser.name

                    // If the current tag is a "note", we need to find the "duration" child tag within it
                    if (tagName == "note") {
                        while (!(eventType == XmlPullParser.END_TAG && parser.name == "note")) {
                            if (eventType == XmlPullParser.START_TAG && parser.name == "duration") {
                                // Parse the duration value
                                eventType = parser.next()
                                // Calculate the quarter note equivalent of the duration
                                val noteDuration = parser.text.toDouble() / divisions * 2.0

                                // Check if adding this note would exceed the allowed duration
                                if (totalQuarterNotes + noteDuration > quarterNotes / ticksPerQuarterNote) {
                                    break
                                }
                                // Update the total duration and note count
                                totalQuarterNotes += noteDuration
                                noteCount++
                            }
                            eventType = parser.next()
                        }
                    }
                    // If the current tag is "divisions", update the divisions value
                    else if (tagName == "divisions") {
                        eventType = parser.next()
                        divisions = parser.text.toInt()
                    }
                }
                eventType = parser.next()
            }

            // Close the input stream after parsing
            inputStream.close()

            // Return the total note count
            return noteCount + 1
        }

    }
}