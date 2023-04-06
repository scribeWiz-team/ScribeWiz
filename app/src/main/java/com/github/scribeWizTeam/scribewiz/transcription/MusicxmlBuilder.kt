package com.github.scribeWizTeam.scribewiz.transcription


class Tag(val name: String, val attr: List<Pair<String, String>> = listOf()){

    fun wrap_content(content: String, depth: Int, oneline: Boolean = false): String{
        val indent = " ".repeat(2*depth)
        val attributes = attr.fold("") { attrStr, elem -> 
            val (key, value) = elem
            "$attrStr $key=\"$value\""
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

class Leaf(val tag: Tag, val content: String): Tree() {

    constructor(name: String, content: String): this(Tag(name), content)

    override fun render(depth: Int): String {
        return tag.wrap_content(content, depth, oneline=true)
    }
}

data class MusicxmlNote(val step: String, val octave: String, val duration: Int){

    fun toNode(): Node {
        return Node("note",
                   Node("pitch",
                       Leaf("step", step),
                       Leaf("octave", octave)
                   ),
                   Leaf("duration", duration.toString()),
                   Leaf("type", "whole")
               )
    }
}


class MusicxmlBuilder(val scoreName: String) {
    var notes: List<MusicxmlNote> = listOf()

    fun add_note(note: MusicxmlNote){
        notes += note
    }

    fun build(): String {
        val header = """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE score-partwise PUBLIC "-//Recordare//DTD MusicXML 4.0 Partwise//EN" "http://www.musicxml.org/dtds/partwise.dtd">"""
        val notesNodes = notes.map({ it -> it.toNode() }).toTypedArray()
        val xmltree = (
            Node("score-partwise".tagattr("version" to "4.0"),
                Node("part-list",
                    Node("score-part".tagattr("id" to "P1"),
                        Leaf("part-name", scoreName)
                    )
                ),
                Node("part".tagattr("id" to "P1"),
                    Node("measure".tagattr("number" to "1"),
                        Node("attributes",
                            Leaf("divisions", "1"),
                            Node("key",
                                Leaf("fifths", "0")
                            ),
                            Node("time",
                                Leaf("beats", "4"),
                                Leaf("beat-type", "4"),
                            ),
                            Node("clef",
                                Leaf("sign", "G"),
                                Leaf("line", "2")
                            )
                        ),
                        *notesNodes
                    )
                )
            )
        )
        val treedata = xmltree.render()
        val res = "$header\n$treedata"
        return res
    }

}
