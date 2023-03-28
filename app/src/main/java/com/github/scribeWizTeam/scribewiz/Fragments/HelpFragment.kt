package com.github.scribeWizTeam.scribewiz.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.R

class HelpFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help, container,false)
    }

}