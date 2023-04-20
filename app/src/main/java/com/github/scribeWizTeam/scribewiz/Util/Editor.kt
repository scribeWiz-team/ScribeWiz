package com.github.scribeWizTeam.scribewiz.Util

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Editor {

    companion object {
        fun editNoteInMusicXML(outputFile: File, inputFile: File, noteLocation: Int, newNote: String) {
            val parserFactory = XmlPullParserFactory.newInstance()
            val parser = parserFactory.newPullParser()
            val inputStream = FileInputStream(inputFile)
            parser.setInput(inputStream, null)

            val xmlSerializer = Xml.newSerializer()
            val outputStream = FileOutputStream(outputFile)
            xmlSerializer.setOutput(outputStream, "UTF-8")
            xmlSerializer.startDocument(null, true)

            var eventType = parser.eventType
            var currentNoteLocation = 0

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name
                        xmlSerializer.startTag(null, tagName)

                        for (i in 0 until parser.attributeCount) {
                            xmlSerializer.attribute(
                                null,
                                parser.getAttributeName(i),
                                parser.getAttributeValue(i)
                            )
                        }

                        if (tagName == "note") {
                            currentNoteLocation++
                        }

                        if (tagName == "step" && currentNoteLocation == noteLocation) {
                            xmlSerializer.text(newNote)
                            eventType = parser.next() // Skip the original note text
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        xmlSerializer.endTag(null, parser.name)
                    }
                    XmlPullParser.TEXT -> {
                        xmlSerializer.text(parser.text)
                    }
                }
                eventType = parser.next()
            }

            xmlSerializer.endDocument()
            inputStream.close()
            outputStream.close()
        }
    }


}