package com.github.scribeWizTeam.scribewiz.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.Fragments.*
import com.github.scribeWizTeam.scribewiz.R
import com.google.android.material.navigation.NavigationView



class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        goHomePage()
    }

    public fun goHomePage() {
        //set the layout
        setContentView(R.layout.activity_navigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //set the action bar
        //setSupportActionBar(toolbar)
        //set the navigation drawer
        drawer = findViewById(R.id.drawer_layout)
        //set the navigation view
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //set the toggle button
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        //set the drawer listener
        drawer.addDrawerListener(toggle)

        toggle.syncState()
        //set the default fragment
        //supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment(0))
        //.commit()
        //set the default item selected
        navigationView.setCheckedItem(R.id.nav_home)
    }

    override fun onBackPressed() {
        // Code to handle the back button press event
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Code to handle the navigation view item click event
        when (item.itemId) {
            R.id.nav_help -> showFragment(HelpFragment(0))
            R.id.nav_home -> showFragment(HomeFragment(0))
            R.id.nav_library -> showFragment(LibraryFragment(0))
            R.id.nav_rec -> showFragment(RecFragment(0))
            R.id.nav_settings -> showFragment(SettingsFragment(0))
            R.id.nav_share -> ShareFragment(0).shareMidiFile("/storage/emulated/0/Android/data/com.github.scribeWizTeam.scribewiz/cache/recording.3gp", this)
        }
        //close the navigation drawer
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    //auxiliary function to show a fragment
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()
    }

}
