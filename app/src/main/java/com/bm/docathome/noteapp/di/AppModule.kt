package com.bm.docathome.noteapp.di

import android.app.Application
import androidx.room.Room
import com.bm.docathome.noteapp.feature_note.data.data_source.NoteDB
import com.bm.docathome.noteapp.feature_note.data.data_source.NoteDao
import com.bm.docathome.noteapp.feature_note.data.repository.NoteRepositoryImp
import com.bm.docathome.noteapp.feature_note.domain.repository.NoteRepository
import com.bm.docathome.noteapp.feature_note.domain.use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDataBase(app:Application):NoteDB{
        return Room.databaseBuilder(
            app,
            NoteDB::class.java,
            NoteDB.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db : NoteDB): NoteRepository{
        return NoteRepositoryImp(db.dao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }

}