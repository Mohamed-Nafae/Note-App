package com.bm.docathome.noteapp.feature_note.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bm.docathome.noteapp.feature_note.domain.model.Note

@Database (
    entities = [Note::class],
    version = 1,
    exportSchema = false
    )
abstract class NoteDB : RoomDatabase() {

    abstract val dao:NoteDao

    companion object{
        const val DATABASE_NAME = "notes_db"
    }
}