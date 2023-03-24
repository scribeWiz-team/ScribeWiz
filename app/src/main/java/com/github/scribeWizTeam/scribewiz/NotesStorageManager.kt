package com.github.scribeWizTeam.scribewiz

import java.io.File

const val MUSIC_XML_EXTENSION : String = "musicxml"
const val NOTES_FOLDER : String = "music_notes"

class NotesStorageManager(_storageFolder: File) {

    private val storageFolder: File

    init {
        if (!_storageFolder.exists()) {
            _storageFolder.mkdir()
        }
        storageFolder = File(_storageFolder, NOTES_FOLDER)
    }

    /**
     *  Return the names of all music notes in the storage folder
     */
    fun notesNames(): List<String> {

        return storageFolder.listFiles()
            ?.filter { f ->
                f.extension == MUSIC_XML_EXTENSION
            }?.map { f ->
                f.name.removeSuffix(".$MUSIC_XML_EXTENSION")
            } ?: emptyList()
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
}