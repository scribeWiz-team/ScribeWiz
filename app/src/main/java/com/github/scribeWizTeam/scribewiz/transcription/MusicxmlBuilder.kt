package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.*


class Tag(val name: String, val attr: List<Pair<String, String>> = listOf()){

    fun wrap_content(content: String?, depth: Int, oneline: Boolean = false): String{
        val indent = " ".repeat(2*depth)
        val attributes = attr.fold("") { attrStr, elem -> 
            val (key, value) = elem
            "$attrStr $key=\"$value\""
        }
        if (content == null){
            return "$indent<$name$attributes/>"
        }
        val headtag = "$indent<$name$attributes>"
        if (oneline){
            val endtag = "</$name>"
            return "$headtag$content$endtag"
        } else {
            val endtag = "$indent</$name>"
            return "$headtag\n$content\n$endtag"
        }
    }
}

fun String.tagattr(vararg attr: Pair<String, String>): Tag {
    return Tag(this, attr.toList())
}

abstract class Tree(){

    abstract fun render(depth: Int = 0): String

}


class Node(val tag: Tag, vararg val childrens: Tree): Tree() {

    constructor(name: String, vararg childrens: Tree): this(Tag(name), *childrens)

    override fun render(depth: Int): String {
        val content = childrens.map({it.render(depth+1)}).joinToString(separator="\n")
        return tag.wrap_content(content, depth)
    }
}

class Leaf(val tag: Tag, val content: String?): Tree() {

    constructor(name: String, content: String): this(Tag(name), content)
    constructor(name: String): this(Tag(name), null)

    override fun render(depth: Int): String {
        return tag.wrap_content(content, depth, oneline=true)
    }
}

interface StaffElement {
    val duration: Int
    val type: String
    val dot: Boolean

    fun toNode(): Node

    fun durationNodes(): Array<Tree> {
        if (dot){
            return arrayOf(
                Leaf("duration", duration.toString()),
                Leaf("type", type),
                Leaf("dot"),
            )
        } else {
            return arrayOf(
                Leaf("duration", duration.toString()),
                Leaf("type", type),
            )
        }
    }
}

data class StaffNote(val step: String, 
                     val octave: Int,
                     val alter: Int, 
                     override val duration: Int,
                     override val type: String, 
                     override val dot: Boolean): StaffElement {

    override fun toNode(): Node {
        val pitchdata: Array<Leaf>
        if (alter == 0){
            pitchdata = arrayOf(
                Leaf("step", step),
                Leaf("octave", octave.toString())
            )
        } else {
            pitchdata = arrayOf(
                Leaf("step", step),
                Leaf("alter", alter.toString()),
                Leaf("octave", octave.toString())
            )
        }
        return Node("note",
                   Node("pitch", *pitchdata),
                   *durationNodes()
               )
    }
}

data class StaffRest(override val duration: Int,
                     override val type: String, 
                     override val dot: Boolean): StaffElement {

    override fun toNode(): Node {
        return Node("note",
                   Leaf("rest"),
                   *durationNodes()
               )
    }
}


data class Signature(val key: Int, val beats: Int, val beat_type: Int,
                     val divisions: Int = 1, val tempo: Int = 120){
    // key: the key to use for the music piece
    //      0 indicates C major
    //      a positive number indicates a number of sharps
    //      a negative number indicates a number of flats
    //
    // beats and beat_type: the time signature for the piece
    //                      e.g. use beats = 3 and beat_type = 4 for a 3/4 piece
    //
    // divisions: controls the smallest representable note will be, possible values are
    //            divisions = 1 (quarter note) [default]
    //            divisions = 2 (eighth note)
    //            divisions = 4 (16th note)
    //
    // tempo: the tempo of the piece, given in bpm, with one quarter note per beat
    val durationNames = listOf(
        "16th",
        "eighth",
        "quarter",
        "half",
        "whole",
    )
    val maxDivisions = 2.0.pow(durationNames.size - 3).toInt()
    val measureMaxDuration: Int = 4*divisions*beats/beat_type

    fun to_absolute_duration(duration: Int): Int{
        return maxDivisions*duration/divisions
    }

    fun to_relative_duration(duration: Int): Int{
        return divisions*duration/maxDivisions
    }

    fun get_representable_duration(duration: Int, measure_time: Int): Triple<String, Boolean, Int>{
        val allowed_duration: Int = min(duration, measureMaxDuration - measure_time)
        val absoluteDuration: Int = to_absolute_duration(allowed_duration)
        var i = 0
        var dur = 1
        while (2*dur <= absoluteDuration){
            dur *= 2
            i += 1
        }
        val dot: Boolean
        if (3*dur <= 2*absoluteDuration){
            dot = true
            dur = (3*dur) / 2
        } else {
            dot = false
        }
        return Triple(durationNames.get(i), dot, to_relative_duration(dur))
    }

    fun get_duration(time: Double): Int {
        return (time * tempo * divisions / 60.0).roundToInt()
    }

    fun toNode(): Node {
        return Node("attributes",
                    Leaf("divisions", divisions.toString()),
                    Node("key",
                        Leaf("fifths", key.toString())
                    ),
                    Node("time",
                        Leaf("beats", beats.toString()),
                        Leaf("beat-type", beat_type.toString()),
                    ),
                    Node("clef",
                        Leaf("sign", "G"),
                        Leaf("line", "2")
                    )
               )
    }
}


class MusicxmlBuilder(val scoreName: String, val signature: Signature) {
    val steps: List<String>
    val alterations: List<Int>
    var staff: List<Node> = listOf()
    var measure: List<StaffElement> = listOf()
    var measureTime: Int = 0

    init {
        if (signature.key >= 0){
            steps = listOf(      "C","C","D","D","E","F","F","G","G","A","A","B")
            alterations = listOf( 0,  1,  0,  1,  0,  0,  1,  0,  1,  0,  1,  0 )
        } else {
            steps = listOf(      "C","D","D","E","E","F","G","G","A","A","B","B")
            alterations = listOf( 0, -1,  0, -1,  0,  0, -1,  0, -1,  0, -1,  0 )
        }
    }

    fun add_note(midinote: MidiNote) {
        var duration = signature.get_duration(midinote.duration)
        var elements: List<StaffElement> = emptyList()
        if (midinote.pitch >= 0){
            val index = midinote.pitch % 12
            val step = steps[index]
            val alter = alterations[index]
            val octave = midinote.pitch / 12 - 1
            while (duration > 0){
                val (type, dot, dur) = signature.get_representable_duration(duration, measureTime)
                val element = StaffNote(step, octave, alter, dur, type, dot)
                duration -= dur
                push_to_measure(element)
            }
        } else {
            while (duration > 0){
                val (type, dot, dur) = signature.get_representable_duration(duration, measureTime)
                val element = StaffRest(dur, type, dot)
                duration -= dur
                push_to_measure(element)
            }
        }
    }

    fun push_to_measure(note: StaffElement){
        measure += note
        measureTime += note.duration
        if (measureTime == signature.measureMaxDuration){
            flush_measure()
        }
    }

    fun flush_measure(){
        if (measureTime == 0){
            return
        }
        val measure_count = staff.size + 1
        val notesNodes = measure.map({ it -> it.toNode() }).toTypedArray()
        if (measure_count == 1){
            staff += Node("measure".tagattr("number" to measure_count.toString()),
                signature.toNode(),
                *notesNodes
            )
        } else {
            staff += Node("measure".tagattr("number" to measure_count.toString()),
                *notesNodes
            )
        }
        measure = emptyList()
        measureTime = 0
    }

    fun build(): String {
        flush_measure()
        val header = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE score-partwise PUBLIC "-//Recordare//DTD MusicXML 4.0 Partwise//EN" "http://www.musicxml.org/dtds/partwise.dtd">"""
        val staffNodes = staff.toTypedArray()
        val xmltree = (
            Node("score-partwise".tagattr("version" to "4.0"),
                Node("part-list",
                    Node("score-part".tagattr("id" to "P1"),
                        Leaf("part-name", scoreName)
                    )
                ),
                Node("part".tagattr("id" to "P1"),
                    *staffNodes
                )
            )
        )
        val treedata = xmltree.render()
        val res = "$header\n$treedata"
        return res
    }

}
