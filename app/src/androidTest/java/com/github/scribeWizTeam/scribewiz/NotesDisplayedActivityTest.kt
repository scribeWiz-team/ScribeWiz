package com.github.scribeWizTeam.scribewiz

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.contracts.ExperimentalContracts


@ExperimentalContracts
@ExperimentalUnsignedTypes
@RunWith(AndroidJUnit4::class)
class NotesDisplayedActivityTest {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    @Before
    fun setUp() {
        // Initialize Intents before each test
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun testLaunchActivityWithIntent(){

        val uriOfFile = getUriFromAsset(context,"BeetAnGeSample.xml")
        val intent = Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)
        intent.putExtra("FILE", uriOfFile.toString())

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use {
            Intents.intended(hasComponent(NotesDisplayedActivity::class.java.name))
        }
    }


    private fun getUriFromAsset(context: Context, assetFileName: String): Uri? {
        val assetManager = context.assets
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        var tempFile: File? = null

        return try {
            inputStream = assetManager.open(assetFileName)
            tempFile = File.createTempFile("temp_asset", null, context.cacheDir)
            outputStream = FileOutputStream(tempFile)

            inputStream.copyTo(outputStream)

            Uri.fromFile(tempFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
            outputStream?.close()
            tempFile?.deleteOnExit()
        }
    }

    @After
    fun end() {
        Intents.release()
    }

}