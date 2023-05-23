package com.github.scribeWizTeam.scribewiz


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.contracts.ExperimentalContracts
import kotlin.test.assertEquals
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

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun testLaunchActivityWithIntent() {

        val uriOfFile = getUriFromAsset(context, "BeetAnGeSample.xml")
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)
        intent.putExtra("FILE", uriOfFile.toString())

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use {
            Intents.intended(hasComponent(NotesDisplayedActivity::class.java.name))
        }
    }

    @Test
    fun exceptionCaughtWhenBadDataFormat() {
        val uriOfFile = getUriFromAsset(context, "bad_format_test.rtf")
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)
        intent.putExtra("FILE", uriOfFile.toString())
        var checkExceptionCaught = false

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use { scenario ->
            scenario.onActivity { checkExceptionCaught = it.exceptionCaught }
            assertTrue(checkExceptionCaught, "Exception is caught")
        }
    }

//    @Test
//    fun playButtonWork() {
//        val uriOfFile = getUriFromAsset(context, "BeetAnGeSample.xml")
//        val intent = Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)
//        intent.putExtra("FILE", uriOfFile.toString())
//
//        ActivityScenario.launch<NotesDisplayedActivity>(intent).use{ scenario ->
//            Thread.sleep(1000) //Let time to the player to display the data
//            onView(withId(R.id.play_button)).perform(click())
//            scenario.onActivity {
//                assertTrue(it.isPlaying(),"The player should be playing" )
//            }
//        }
//    }


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

    @Test
    fun testCreateTempFileFromUri() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val inputXMLContent = """
        <?xml version="1.0"?>
        <score>
            <note>
                <pitch>
                    <step>A</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>B</step>
                </pitch>
            </note>
            <note>
                <pitch>
                    <step>C</step>
                </pitch>
            </note>
        </score>
    """.trimIndent()

        val inputFile = File.createTempFile("temp_musicxml_input", ".xml", context.cacheDir)
        inputFile.writeText(inputXMLContent)

        var scenario: ActivityScenario<NotesDisplayedActivity>? = null
        scenario =
            ActivityScenario.launch(Intent(context, NotesDisplayedActivity::class.java).apply {
                putExtra("FILE", inputFile.toURI().toString())
            })

        scenario.onActivity { activity ->
            val inputFileUri = Uri.parse(inputFile.toURI().toString())

            val tempFile = NotesDisplayedActivity.createTempFileFromUri(activity, inputFileUri)

            val inputXMLContent = inputFile.readText()
            val outputXMLContent = tempFile.readText()
            assertEquals(inputXMLContent, outputXMLContent)
        }

        scenario.close()

        inputFile.delete()
    }

    @Test
    fun testSpinnerDisplay() {
        // Prepare your intent as necessary
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use { scenario ->
            // Check the spinner is displayed and visible
            onView(withId(R.id.note_spinner))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }

    @Test
    fun testSpinnerSelectionAndButtonClick() {
        // Prepare your intent as necessary
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)

        ActivityScenario.launch<NotesDisplayedActivity>(intent).use { scenario ->
            // Select an item from the spinner.
            // This clicks the spinner, waits for it to open, and then clicks on the item at index 3.
            onView(withId(R.id.note_spinner)).perform(click())
            onData(anything()).atPosition(3).perform(click())

            // Verify the spinner's selected item.
            onView(withId(R.id.note_spinner)).check(matches(withSpinnerText(containsString("D#"))))

            // Click the button
            onView(withId(R.id.replace_note_button)).perform(click())

        }
    }


}