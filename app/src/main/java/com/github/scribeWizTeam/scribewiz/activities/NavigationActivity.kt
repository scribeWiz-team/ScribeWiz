package com.github.scribeWizTeam.scribewiz.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.fragments.*
import com.google.android.material.navigation.NavigationView


/**
 * An activity that handles navigation and displays various fragments.
 */
class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        goHomePage()
    }

    /**
     * Sets up the home page with the navigation drawer.
     */
    private fun goHomePage() {
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
        navigationView.setCheckedItem(R.id.nav_profile)
        val from = intent
        when (from.getStringExtra("fragment")) {
            "profilePage" -> showFragment(ProfilePageFragment())
            "recordPage" -> showFragment(RecParameterFragment())
            "helpPage" -> showFragment(HelpFragment())
            "challengesPage" -> showFragment(ChallengesFragment())
            else -> showFragment(ProfilePageFragment())
        }
    }

    /**
     * Handles the back button press event.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Handles the navigation view item click event.
     *
     * @param item The selected menu item.
     * @return True if the event was handled, false otherwise.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Code to handle the navigation view item click event
        when (item.itemId) {
            R.id.home -> {
                val home = Intent(this, MainActivity::class.java)
                startActivity(home)
            }

            R.id.nav_library -> showFragment(NotesListFragment())
            R.id.nav_profile -> showFragment(ProfilePageFragment())
            R.id.nav_help -> showFragment(HelpFragment())
            R.id.nav_rec -> showFragment(RecParameterFragment())
            R.id.nav_share -> ShareFragment().shareMidiFile(
                "/storage/emulated/0/Android/data/com.github.scribeWizTeam.scribewiz/cache/recording.3gp",
                this
            )

            R.id.nav_challenges -> showFragment(ChallengesFragment())
        }
        //close the navigation drawer
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Displays the specified fragment.
     *
     * @param fragment The fragment to be displayed.
     */
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()
    }

}
