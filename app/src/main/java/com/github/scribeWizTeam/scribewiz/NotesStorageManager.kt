package com.github.scribeWizTeam.scribewiz

import android.content.Context
import android.util.Log
import java.io.File


const val MUSIC_XML_EXTENSION : String = "xml"
const val NOTES_FOLDER : String = "music_notes"

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
    fun renameFile(oldName: String, newName: String) : Boolean{
        val file: File? = getNoteFile(oldName)
        var fileHasBeenSuccessfullyRenamed = false

        when (file) {
            is File -> fileHasBeenSuccessfullyRenamed = file.renameTo(File(storageFolder,"$newName.$MUSIC_XML_EXTENSION"))
            else -> {Log.e("errorTag","The file couldn't be loaded")
                     throw Exception("The file couldn't be found")}
        }

        return fileHasBeenSuccessfullyRenamed
    }
}