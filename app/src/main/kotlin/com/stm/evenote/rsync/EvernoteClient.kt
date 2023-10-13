package com.stm.evenote.rsync

import com.evernote.clients.NoteStoreClient
import com.evernote.edam.notestore.NoteFilter
import com.evernote.edam.type.Note
import com.evernote.edam.type.Notebook
import com.evernote.edam.type.Resource
import com.stm.evenote.rsync.model.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayList

class EvernoteClient(private val noteStoreClient: NoteStoreClient) {

    private val notebooks: MutableList<Notebook> = ArrayList()

    fun loadAllNotebooks(): EvernoteClient {
        return try {
            println("Loading complete notebook list")
            notebooks.clear()
            notebooks.addAll(noteStoreClient.listNotebooks())
            println("Found ${notebooks.size} notebooks.")
            this
        } catch (e: Exception) {
            throw RuntimeException("Could not load evernote notebooks.", e)
        }
    }

    fun filterForNotebooks(notebookNames : List<String?>): EvernoteClient {
        if (notebookNames.isNotEmpty()){
            notebooks.removeIf{!notebookNames.contains(it.name)}
        }
        return this
    }

    fun filterForStacknames(stacks : List<String?>): EvernoteClient {
        if (stacks.isNotEmpty()) {
            notebooks.removeIf{!stacks.contains(it.stack)}
        }
        return this
    }

    fun export(localDirectory: String) {

        println("Filter applied and found ${notebooks.size} notebooks to export")
        var counter = 0
        val stacks = notebookStream().map { it.stack }.distinct().toList()
        for (stack in stacks) {
            val notebooks = notebookStream().filter { it.stack == stack }.toList()

            if (notebooks.isEmpty()) {
                println("Stack '${stack}' should be exported, but no notebooks found in that stack - skipping.")
                continue
            }
            val folder = File("${localDirectory}/${stack ?: ""}")
            folder.mkdirs()

            println("Found ${notebooks.size} notebooks in stack '${stack ?: ""}'. Export will be saved in directory '${folder.absolutePath}'")
            for (notebook in notebooks) {
                counter ++
                println("Exporting notebook (${counter} of ${this.notebooks.size}): '${notebook.name}' ")
                val enexFile = "${folder.absolutePath}/${notebook.name.replace("/", "_")}.enex"
                var writer = SimpleXmlWriter(BufferedWriter(FileWriter(enexFile)),
                    true)
                var enexExport = EnexFile(writer)

                export(notebook, enexExport)
                enexExport.close()
                writer.close()
                println("Export of notebook finished, created file: '${enexFile}'")
            }
        }
    }

    private fun export(notebook: Notebook, enexFile: EnexFile) {
        val notes = loadNotesIn(notebook, 0)
        println("Found ${notes.size} notes to export in notebook '${notebook.name}'")
        var counter = 0
        for (note in notes) {
            counter ++
            val tags = noteStoreClient.getNoteTagNames(note.guid)
            println("Exporting note (${counter} of ${notes.size}): '${note.title}' in notebook '${notebook.name}' ")
            val content = noteStoreClient.getNoteContent(note.guid)

            var enexNote = EnexNote(
                note.title,
                content,
                note.created,
                note.updated,
                tags,
                EnexNoteAttributes(
                    note.attributes.latitude,
                    note.attributes.longitude,
                    note.attributes.altitude,
                    note.attributes.author ?: "",
                    note.attributes.contentClass,
                    note.attributes.source,
                    note.attributes.sourceURL,
                    note.attributes.sourceApplication,
                    note.attributes.reminderDoneTime,
                    note.attributes.reminderTime,
                    note.attributes.reminderOrder,

                    ),
                (note.resources?: Collections.emptyList()).filterNotNull().stream()
                    .map { it!! }
                    .map { toEnexResource(it) }
                    .toList()
            )
            enexFile.addNote(enexNote)
        }
    }

    private fun toEnexResource(resource: Resource) : EnexResource {
        return EnexResource(
            loadBinaryDataFrom(resource.guid),
            resource.mime,
            resource.width.toInt(),
            resource.height.toInt(),
            EnexResourceAttributes(
                resource.attributes.fileName ?: "",
                resource.attributes.sourceURL ?: "",
                resource.attributes.latitude,
                resource.attributes.longitude,
                resource.attributes.altitude,
                resource.attributes.isAttachment,
            )
        )
    }

    fun loadBinaryDataFrom(resourceGuid: String?): ByteArray {
        return try {
            val resource = noteStoreClient.getResource(
                resourceGuid, true,
                false, false, false
            )
            resource.data.body
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun notebookStream(): Stream<Notebook> {
        if (notebooks.isEmpty()) {
            println("Notebooks collection is empty. No notebooks found. Nothing to export")
        }
        return notebooks.stream()
    }

    private fun loadNotesIn(notebook: Notebook, offset: Int): List<Note> {
        val noteFilter = NoteFilter()
        noteFilter.notebookGuid = notebook.guid
        return try {
            val maxNotes = 100
            val notes = noteStoreClient.findNotes(noteFilter, offset, maxNotes)
            val noteList = ArrayList<Note>()
            if (notes.notesSize > 0) {
                noteList.addAll(notes.notes)
            }
            if (notes.notesSize >= maxNotes) {
                noteList.addAll(loadNotesIn(notebook, offset + notes.notesSize))
            }
            noteList
        } catch (e: Exception) {
            throw RuntimeException("Could not load notes for notebook '${notebook.name}' with guid ${notebook.guid}", e)
        }
    }
}