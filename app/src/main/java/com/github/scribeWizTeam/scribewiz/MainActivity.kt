package com.github.scribeWizTeam.scribewiz

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set the layout
        setContentView(R.layout.activity_main)

        val gotButton = findViewById<Button>(R.id.mainGoButton)
        val nameText = findViewById<EditText>(R.id.mainName)
        //set the toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        //set the action bar
        setSupportActionBar(toolbar)
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

        gotButton.setOnClickListener {
            //set the event you want to perform when button is clicked
            //you can go to another activity in your app by creating Intent
            val intent = Intent(this, GreetingActivity::class.java)
            intent.putExtra("name", nameText.text.toString())
            startActivity(intent)
        }
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
            R.id.nav_help -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HelpFragment(0)).commit()
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment(0)).commit()
            R.id.nav_library -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LibraryFragment(0)).commit()
            R.id.nav_rec -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecFragment(0)).commit()
            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment(0)).commit()
            R.id.nav_share -> Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
        }
        //close the navigation drawer
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}


