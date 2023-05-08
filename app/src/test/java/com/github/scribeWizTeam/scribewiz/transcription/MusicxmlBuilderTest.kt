package com.github.scribeWizTeam.scribewiz.transcription

import org.junit.Test

import org.junit.Assert.*


class MusicxmlBuilderTest {

    @Test
    fun note_conversion_from_midi() {
        val signature = Signature(0, 200, 4, divisions=2, tempo=120)
        val builder = MusicxmlBuilder("Test conversion", signature)
        val inputs = listOf(
            MidiNote(60, 0.0, 2.0),
            MidiNote(67, 0.0, 0.5),
            MidiNote(42, 0.0, 1.5),
            MidiNote(-1, 0.0, 3.0),
        )
        val expected_notes = listOf(
            StaffNote("C", 4, 0, 8, "whole", false),
            StaffNote("G", 4, 0, 2, "quarter", false),
            StaffNote("F", 2, 1, 6, "half", true),
            StaffRest(12, "whole", true),
        )

        for (input in inputs) {
            builder.add_note(input)
        }
        for ((expected, res) in expected_notes zip builder.measure){
            assertEquals(expected, res)
        }
    }

    @Test
    fun simple_score_with_one_note() {
        val expected = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE score-partwise PUBLIC "-//Recordare//DTD MusicXML 4.0 Partwise//EN" "http://www.musicxml.org/dtds/partwise.dtd">
<score-partwise version="4.0">
  <part-list>
    <score-part id="P1">
      <part-name>Test music</part-name>
    </score-part>
  </part-list>
  <part id="P1">
    <measure number="1">
      <attributes>
        <divisions>1</divisions>
        <key>
          <fifths>0</fifths>
        </key>
        <time>
          <beats>4</beats>
          <beat-type>4</beat-type>
        </time>
        <clef>
          <sign>G</sign>
          <line>2</line>
        </clef>
      </attributes>
      <direction placement="above">
        <direction-type>
          <metronome parentheses="no">
            <beat-unit>quarter</beat-unit>
            <per-minute>60</per-minute>
          </metronome>
        </direction-type>
        <sound tempo="60"/>
      </direction>
      <note>
        <pitch>
          <step>C</step>
          <octave>4</octave>
        </pitch>
        <duration>4</duration>
        <type>whole</type>
      </note>
    </measure>
  </part>
</score-partwise>"""
        val signature = Signature(0, 4, 4, tempo=60)
        val builder = MusicxmlBuilder("Test music", signature)
        builder.add_note(MidiNote(60, 0.0, 4.0))

        assertEquals(expected, builder.build())
    }

    @Test
    fun complex_score_with_many_features() {
        val expected = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE score-partwise PUBLIC "-//Recordare//DTD MusicXML 4.0 Partwise//EN" "http://www.musicxml.org/dtds/partwise.dtd">
<score-partwise version="4.0">
  <part-list>
    <score-part id="P1">
      <part-name>A more complex example</part-name>
    </score-part>
  </part-list>
  <part id="P1">
    <measure number="1">
      <attributes>
        <divisions>2</divisions>
        <key>
          <fifths>-3</fifths>
        </key>
        <time>
          <beats>6</beats>
          <beat-type>8</beat-type>
        </time>
        <clef>
          <sign>G</sign>
          <line>2</line>
        </clef>
      </attributes>
      <direction placement="above">
        <direction-type>
          <metronome parentheses="no">
            <beat-unit>quarter</beat-unit>
            <per-minute>60</per-minute>
          </metronome>
        </direction-type>
        <sound tempo="60"/>
      </direction>
      <note>
        <pitch>
          <step>G</step>
          <octave>4</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
      <note>
        <pitch>
          <step>A</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>1</duration>
        <type>eighth</type>
      </note>
      <note>
        <pitch>
          <step>B</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>2</duration>
        <type>quarter</type>
      </note>
    </measure>
    <measure number="2">
      <note>
        <pitch>
          <step>C</step>
          <octave>5</octave>
        </pitch>
        <duration>4</duration>
        <type>half</type>
      </note>
      <note>
        <rest/>
        <duration>1</duration>
        <type>eighth</type>
      </note>
      <note>
        <pitch>
          <step>A</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>1</duration>
        <type>eighth</type>
      </note>
    </measure>
    <measure number="3">
      <note>
        <pitch>
          <step>A</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
      <note>
        <rest/>
        <duration>2</duration>
        <type>quarter</type>
      </note>
      <note>
        <pitch>
          <step>B</step>
          <octave>4</octave>
        </pitch>
        <duration>1</duration>
        <type>eighth</type>
      </note>
    </measure>
    <measure number="4">
      <note>
        <pitch>
          <step>C</step>
          <octave>5</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
      <note>
        <pitch>
          <step>D</step>
          <alter>-1</alter>
          <octave>5</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
    </measure>
    <measure number="5">
      <note>
        <pitch>
          <step>D</step>
          <octave>4</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
      <note>
        <pitch>
          <step>B</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
    </measure>
    <measure number="6">
      <note>
        <pitch>
          <step>F</step>
          <octave>4</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
      <note>
        <pitch>
          <step>G</step>
          <octave>4</octave>
        </pitch>
        <duration>3</duration>
        <type>quarter</type>
        <dot/>
      </note>
    </measure>
    <measure number="7">
      <note>
        <pitch>
          <step>E</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>1</duration>
        <type>eighth</type>
      </note>
      <note>
        <pitch>
          <step>D</step>
          <octave>4</octave>
        </pitch>
        <duration>1</duration>
        <type>eighth</type>
      </note>
      <note>
        <pitch>
          <step>E</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>1</duration>
        <type>eighth</type>
      </note>
      <note>
        <pitch>
          <step>D</step>
          <octave>4</octave>
        </pitch>
        <duration>1</duration>
        <type>eighth</type>
      </note>
      <note>
        <pitch>
          <step>E</step>
          <alter>-1</alter>
          <octave>4</octave>
        </pitch>
        <duration>2</duration>
        <type>quarter</type>
      </note>
    </measure>
    <measure number="8">
      <note>
        <pitch>
          <step>B</step>
          <alter>-1</alter>
          <octave>3</octave>
        </pitch>
        <duration>6</duration>
        <type>half</type>
        <dot/>
      </note>
    </measure>
  </part>
</score-partwise>"""
        val signature = Signature(-3, 6, 8, divisions=2, tempo=60)
        val builder = MusicxmlBuilder("A more complex example", signature)
        val notes = listOf(
                MidiNote(67, 0.0, 1.5),
                MidiNote(68, 0.0, 0.5),
                MidiNote(70, 0.0, 1.0),
                MidiNote(72, 0.0, 2.0),
                MidiNote(-1, 0.0, 0.5),
                MidiNote(68, 0.0, 0.5),
                MidiNote(68, 0.0, 1.5),
                MidiNote(-1, 0.0, 1.0),
                MidiNote(71, 0.0, 0.5),
                MidiNote(72, 0.0, 1.5),
                MidiNote(73, 0.0, 1.5),
                MidiNote(62, 0.0, 1.5),
                MidiNote(70, 0.0, 1.5),
                MidiNote(65, 0.0, 1.5),
                MidiNote(67, 0.0, 1.5),
                MidiNote(63, 0.0, 0.5),
                MidiNote(62, 0.0, 0.5),
                MidiNote(63, 0.0, 0.5),
                MidiNote(62, 0.0, 0.5),
                MidiNote(63, 0.0, 1.0),
                MidiNote(58, 0.0, 3.0),
        )
        for (note in notes){
            builder.add_note(note)
        }

        assertEquals(expected, builder.build())
    }

}
