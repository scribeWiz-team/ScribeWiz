package com.github.scribeWizTeam.scribewiz.util


class RecordingParameters {
    companion object {
        const val DEFAULT_SCORE_NAME = ""
        const val FALLBACK_SCORE_NAME = "new_score"
        const val DEFAULT_TONALITY = 0 // C major
        const val DEFAULT_BEATS = 4
        const val DEFAULT_BEAT_TYPE = 4
        const val DEFAULT_TEMPO = 120
        const val DEFAULT_KEY_SIGNATURE = false // use F key
    }

    var scoreName: String = DEFAULT_SCORE_NAME
    var fifths: Int = DEFAULT_TONALITY
    var beats: Int = DEFAULT_BEATS
    var beatType: Int = DEFAULT_BEAT_TYPE
    var tempo: Int = DEFAULT_TEMPO
    var useGKeySignature: Boolean = DEFAULT_KEY_SIGNATURE
}
