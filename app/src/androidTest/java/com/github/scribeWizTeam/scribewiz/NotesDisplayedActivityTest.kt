package com.github.scribeWizTeam.scribewiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import okhttp3.internal.wait
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.contracts.ExperimentalContracts
import kotlin.test.assertTrue


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
    @Test
    fun testLaunchActivityWithIntent(){

        val uriOfFile = getUriFromAsset(context,"BeetAnGeSample.xml")
        val intent = Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)
        intent.putExtra("FILE", uriOfFile.toString())

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use {
            Intents.intended(hasComponent(NotesDisplayedActivity::class.java.name))
        }
    }

    @Test
    fun exceptionCaughtWhenBadDataFormat() {
        val uriOfFile = getUriFromAsset(context, "bad_format_test.rtf")
        val intent = Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)
        intent.putExtra("FILE", uriOfFile.toString())
        var checkExceptionCaught = false

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use { scenario ->
            scenario.onActivity { checkExceptionCaught = it.exceptionCaught}
            assertTrue(checkExceptionCaught, "Exception is caught")
        }
    }

    @Test
    fun playButtonWork() {
        val uriOfFile = getUriFromAsset(context, "BeetAnGeSample.xml")
        val intent = Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)
        intent.putExtra("FILE", uriOfFile.toString())

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use{ scenario ->
            Thread.sleep(10000) //Let time to the player to display the data
            onView(withId(R.id.play_button)).perform(click())
            scenario.onActivity {
                assertTrue(it.isPlaying(),"The player should be playing" )
            }
        }
    }
    //

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