package com.github.scribeWizTeam.scribewiz

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream


const val MUSIC_XML_EXTENSION: String = "xml"
const val NOTES_FOLDER: String = "music_notes"
const val FILES_COLLECTION_DB: String = "Files"

class NotesStorageManager() {

    private lateinit var storageFolder: File

    constructor(file: File) : this() {
        storageFolder = file
    }

    constructor(context: Context) : this() {
        storageFolder = context.getExternalFilesDir(NOTES_FOLDER)?.absoluteFile!!
        storageFolder.mkdir()
    }

    /**
     *  Return the names of all music notes in the storage folder
     */
    fun getNotesNames(): List<String> {

        return storageFolder.listFiles()
            ?.filter { f ->
                f.extension == MUSIC_XML_EXTENSION
            }?.map { f ->
                f.name.removeSuffix(".$MUSIC_XML_EXTENSION")
            } ?: emptyList()
    }

    /**
     * Return the file corresponding to the name or null if the file does not exist
     */
    fun getNoteFile(name: String): File? {
        val file = storageFolder.resolve("$name.$MUSIC_XML_EXTENSION")
        return if (file.exists()) file else null
    }

    /**
     *  Return the list of the music notes files
     */
    fun getAllNotesFiles(): Array<out File>? {
        return storageFolder.listFiles()
    }

    /**
     *  Write some content to a file
     *
     *  @param name: the name of the file
     *  @param content: the content of the file
     */
    fun writeNoteFile(name: String, content: String) {
        val file = File(storageFolder, "$name.$MUSIC_XML_EXTENSION")
        FileOutputStream(file).use {
            it.write(content.toByteArray())
        }
    }

    /**
     *  Delete a file with name "name.musicxml"
     *
     *  @param name: the name of the file
     */
    fun deleteNote(name: String) {
        File(storageFolder, "$name.$MUSIC_XML_EXTENSION").delete()
    }

    /**
     * Delete the storage folder and its children
     */
    fun clearFolder() {
        storageFolder.deleteRecursively()
    }

    /**
     * Rename the file with oldName by newName
     * Returns true if the renaming has succeeded
     * */
    fun renameFile(oldName: String, newName: String): Boolean {
        val file: File? = getNoteFile(oldName)
        val fileHasBeenSuccessfullyRenamed: Boolean

        when (file) {
            is File -> fileHasBeenSuccessfullyRenamed =
                file.renameTo(File(storageFolder, "$newName.$MUSIC_XML_EXTENSION"))
            else -> {
                Log.e("errorTag", "The file couldn't be loaded")
                throw Exception("The file couldn't be found")
            }
        }

        return fileHasBeenSuccessfullyRenamed
    }

    /** EXAMPLE USAGES

    TO UPLOAD:

    val notesStorageManager = NotesStorageManager(context)
    notesStorageManager.addFileToDatabase("BeetAnGeSample")

    TO DOWNLOAD:
    notesStorageManager.downloadFileFromDatabase("UC9ixjIeXnptB0pHtOiX")

     **/

    /**
     * Uploads a local file to the database file storage
     * @param filename The local name of the file
     */
    fun uploadFileToDatabase(filename: String) {
        val file = getNoteFile(filename)
        val doc = Firebase.firestore.collection(FILES_COLLECTION_DB).document()
        doc.set(
            mutableMapOf(
                "fileID" to doc.id,
                "filename" to filename,
                "content" to file!!.readText(Charsets.UTF_8)
            )
        )
    }

    /**
     * Downloads a file from the database file storage
     * @param fileID The ID of the file in the database Files collection
     */
    fun downloadFileFromDatabase(fileID: String) {
        runBlocking {
            val job = launch {
                Firebase.firestore.collection(FILES_COLLECTION_DB)
                    .document(fileID)
                    .get()
                    .await()
                    .let {
                        File(
                            storageFolder, it.get("filename")!!.toString()
                                    + "_DOWNLOADED" + MUSIC_XML_EXTENSION
                        )
                            .writeText(it.get("content")!!.toString())
                    }
            }
            job.join()
        }
    }
}

