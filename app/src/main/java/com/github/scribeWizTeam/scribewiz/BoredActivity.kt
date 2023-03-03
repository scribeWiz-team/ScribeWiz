package com.github.scribeWizTeam.scribewiz
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BoredActivity (
    val activity: String,
    val type: String,
    val participants: Int,
    val price: Float,
    val link: String,
    @PrimaryKey
    val key: String,
    val accessibility: Float
)