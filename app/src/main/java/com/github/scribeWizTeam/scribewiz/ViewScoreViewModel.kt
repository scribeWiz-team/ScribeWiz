package com.github.scribeWizTeam.scribewiz

import alphaTab.LayoutMode
import alphaTab.Settings
import alphaTab.model.Track
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.contracts.ExperimentalContracts

@ExperimentalUnsignedTypes
@ExperimentalContracts
class ViewScoreViewModel : ViewModel() {
    val currentTickPosition = MutableLiveData<Int>().apply {
        value = 0
    }

    val tracks = MutableLiveData<Iterable<Track>?>()
    val settings = MutableLiveData<Settings>().apply {
        value = Settings().apply {
            this.player.enableCursor = true
            this.player.enablePlayer = true
            this.player.enableUserInteraction = true
            this.display.barCountPerPartial = 4.0
        }
    }

    fun updateLayout(screenWidthDp:Float) {
        if (screenWidthDp >= 600f) {
            settings.value!!.display.layoutMode = LayoutMode.Page
        } else {
            settings.value!!.display.layoutMode = LayoutMode.Horizontal
        }
        settings.value = settings.value // fire change
    }
}
