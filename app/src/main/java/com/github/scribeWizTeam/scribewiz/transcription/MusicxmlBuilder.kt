package com.github.scribeWizTeam.scribewiz.transcription

import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


class Tag(val name: String, private val attr: List<Pair<String, String>> = listOf()) {

    fun wrapContent(content: String?, depth: Int, oneLine: Boolean = false): String {
        val indent = " ".repeat(2 * depth)
        val attributes = attr.fold("") { attrStr, elem ->
            val (key, value) = elem
            "$attrStr $key=\"$value\""
        }
        if (content == null) {
            return "$indent<$name$attributes/>"
        }
        val headTag = "$indent<$name$attributes>"
        return if (oneLine) {
            val endTag = "</$name>"
            "$headTag$content$endTag"
        } else {
            val endTag = "$indent</$name>"
            "$headTag\n$content\n$endTag"
        }
    }
}

fun String.tagAttr(vararg attr: Pair<String, String>): Tag {
    return Tag(this, attr.toList())
}

abstract class Tree {

    abstract fun render(depth: Int = 0): String

}


class Node(private val tag: Tag, private vararg val children: Tree) : Tree() {

    constructor(name: String, vararg children: Tree) : this(Tag(name), *children)

    override fun render(depth: Int): String {
        val content = children.joinToString(separator = "\n") { it.render(depth + 1) }
        return tag.wrapContent(content, depth)
    }
}

class Leaf(private val tag: Tag, val content: String?) : Tree() {

    constructor(name: String, content: String) : this(Tag(name), content)
    constructor(name: String) : this(Tag(name), null)

    constructor(tag: Tag) : this(tag, null)

    override fun render(depth: Int): String {
        return tag.wrapContent(content, depth, oneLine = true)
    }
}

interface StaffElement {
    val duration: Int
    val type: String
    val dot: Boolean

    fun toNode(): Node

    fun durationNodes(): Array<Tree> {
        return if (dot) {
            arrayOf(
                Leaf("duration", duration.toString()),
                Leaf("type", type),
                Leaf("dot"),
            )
        } else {
            arrayOf(
                Leaf("duration", duration.toString()),
                Leaf("type", type),
            )
        }
    }
}

data class StaffNote(
    val step: String,
    val octave: Int,
    val alter: Int,
    override val duration: Int,
    override val type: String,
    override val dot: Boolean
) : StaffElement {

    override fun toNode(): Node {
        val pitchData: Array<Leaf> = if (alter == 0) {
            arrayOf(
                Leaf("step", step),
                Leaf("octave", octave.toString())
            )
        } else {
            arrayOf(
                Leaf("step", step),
                Leaf("alter", alter.toString()),
                Leaf("octave", octave.toString())
            )
        }
        return Node(
            "note",
            Node("pitch", *pitchData),
            *durationNodes()
        )
    }
}

data class StaffRest(
    override val duration: Int,
    override val type: String,
    override val dot: Boolean
) : StaffElement {

    override fun toNode(): Node {
        return Node(
            "note",
            Leaf("rest"),
            *durationNodes()
        )
    }
}


/**
 * The signature of a music piece
 *
 * @param key the key to use for the music piece
 *      0 indicates C major
 *      a positive number indicates a number of sharps
 *      a negative number indicates a number of flats
 *
 * @param beats the upper number of the time signature for the piece
 * @param beatType the lower number of the time signature for the piece
 *      e.g. use beats = 3 and beat_type = 4 for a 3/4 piece
 *
 * @param divisions controls the smallest representable note will be, possible values are
 *      divisions = 1 (quarter note) [default]
 *      divisions = 2 (eighth note)
 *      divisions = 4 (16th note)
 *
 * @param tempo the tempo of the piece, given in bpm, with one quarter note per beat
 */
data class Signature(
    val key: Int, val beats: Int, val beatType: Int,
    val divisions: Int = 1, val tempo: Int = 120,
    val useGKeySignature: Boolean = true
) {
    private val durationNames = listOf(
        "16th",
        "eighth",
        "quarter",
        "half",
        "whole",
    )
    private val maxDivisions = 2.0.pow(durationNames.size - 3).toInt()
    val measureMaxDuration: Int = 4 * divisions * beats / beatType

    private fun toAbsoluteDuration(duration: Int): Int {
        return maxDivisions * duration / divisions
    }

    private fun toRelativeDuration(duration: Int): Int {
        return divisions * duration / maxDivisions
    }

    fun getRepresentableDuration(duration: Int, measure_time: Int): Triple<String, Boolean, Int> {
        val allowedDuration: Int = min(duration, measureMaxDuration - measure_time)
        val absoluteDuration: Int = toAbsoluteDuration(allowedDuration)
        var i = 0
        var dur = 1
        while (2 * dur <= absoluteDuration) {
            dur *= 2
            i += 1
        }
        val dot: Boolean
        if (3 * dur <= 2 * absoluteDuration) {
            dot = true
            dur = (3 * dur) / 2
        } else {
            dot = false
        }
        return Triple(durationNames[i], dot, toRelativeDuration(dur))
    }

    fun getDuration(time: Double): Int {
        return (time * tempo * divisions / 60.0).roundToInt()
    }

    fun toNodes(): Array<Node> {
        val keySig: Node = if (useGKeySignature) {
            Node(
                "clef",
                Leaf("sign", "G"),
                Leaf("line", "2")
            )
        } else {
            Node(
                "clef",
                Leaf("sign", "F"),
                Leaf("line", "4")
            )
        }
        return arrayOf(
            Node(
                "attributes",
                Leaf("divisions", divisions.toString()),
                Node(
                    "key",
                    Leaf("fifths", key.toString())
                ),
                Node(
                    "time",
                    Leaf("beats", beats.toString()),
                    Leaf("beat-type", beatType.toString()),
                ),
                keySig
            ),
            Node(
                "direction".tagAttr("placement" to "above"),
                Node(
                    "direction-type",
                    Node(
                        "metronome".tagAttr("parentheses" to "no"),
                        Leaf("beat-unit", "quarter"),
                        Leaf("per-minute", tempo.toString())
                    )
                ),
                Leaf("sound".tagAttr("tempo" to tempo.toString()))
            )
        )
    }
}

interface MusicRenderer {

    fun addNote(midiNote: MidiNote)

    fun build(): String

    fun reset()
}


/**
 * Build a musicxml document from a sequence of notes
 *
 * @param scoreName the name of this musical score
 * @param signature the signature of this music score, see {@link com.github.scribeWizTeam.scribewiz.transcription.MusicxmlBuilder.Signature Signature}
 */
class MusicxmlBuilder(private val scoreName: String, private val signature: Signature) :
    MusicRenderer {

    private val steps: List<String>
    private val alterations: List<Int>
    private var staff: MutableList<Node> = mutableListOf()
    var measure: MutableList<StaffElement> = mutableListOf()
    private var measureTime: Int = 0

    init {
        if (signature.key >= 0) {
            steps = listOf("C", "C", "D", "D", "E", "F", "F", "G", "G", "A", "A", "B")
            alterations = listOf(0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0)
        } else {
            steps = listOf("C", "D", "D", "E", "E", "F", "G", "G", "A", "A", "B", "B")
            alterations = listOf(0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, 0)
        }
    }

    override fun reset() {
        staff = mutableListOf()
        measure = mutableListOf()
        measureTime = 0
    }

    override fun addNote(midiNote: MidiNote) {
        var duration = signature.getDuration(midiNote.duration)
        if (midiNote.pitch >= 0) {
            val index = midiNote.pitch % 12
            val step = steps[index]
            val alter = alterations[index]
            val octave = midiNote.pitch / 12 - 1
            while (duration > 0) {
                val (type, dot, dur) = signature.getRepresentableDuration(duration, measureTime)
                val element = StaffNote(step, octave, alter, dur, type, dot)
                duration -= dur
                pushToMeasure(element)
            }
        } else {
            while (duration > 0) {
                val (type, dot, dur) = signature.getRepresentableDuration(duration, measureTime)
                val element = StaffRest(dur, type, dot)
                duration -= dur
                pushToMeasure(element)
            }
        }
    }

    private fun pushToMeasure(note: StaffElement) {
        measure.add(note)
        measureTime += note.duration
        if (measureTime == signature.measureMaxDuration) {
            flushMeasure()
        }
    }

    private fun flushMeasure() {
        if (measureTime == 0) {
            return
        }
        val measureCount = staff.size + 1
        val notesNodes = measure.map { it.toNode() }.toTypedArray()
        staff += if (measureCount == 1) {
            Node(
                "measure".tagAttr("number" to measureCount.toString()),
                *signature.toNodes(),
                *notesNodes
            )
        } else {
            Node(
                "measure".tagAttr("number" to measureCount.toString()),
                *notesNodes
            )
        }
        measure = mutableListOf()
        measureTime = 0
    }

    override fun build(): String {
        flushMeasure()
        val header = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE score-partwise PUBLIC "-//Recordare//DTD MusicXML 4.0 Partwise//EN" "http://www.musicxml.org/dtds/partwise.dtd">"""
        val staffNodes = staff.toTypedArray()
        val xmltree = (
                Node(
                    "score-partwise".tagAttr("version" to "4.0"),
                    Node(
                        "part-list",
                        Node(
                            "score-part".tagAttr("id" to "P1"),
                            Leaf("part-name", scoreName)
                        )
                    ),
                    Node(
                        "part".tagAttr("id" to "P1"),
                        *staffNodes
                    )
                )
                )
        val treeData = xmltree.render()
        return "$header\n$treeData"
    }

}
