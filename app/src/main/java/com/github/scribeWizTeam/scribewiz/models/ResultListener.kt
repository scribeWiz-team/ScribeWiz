package com.github.scribeWizTeam.scribewiz.models

interface ResultListener {
    fun onSuccess()
    fun onError(error: Throwable)
}