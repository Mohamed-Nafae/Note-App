package com.bm.docathome.noteapp.feature_note.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bm.docathome.noteapp.feature_note.presentation.add_edit_notes.AddEditNoteScreen
import com.bm.docathome.noteapp.feature_note.presentation.add_edit_notes.AddEditNoteViewModel
import com.bm.docathome.noteapp.feature_note.presentation.notes.NotesScreen
import com.bm.docathome.noteapp.feature_note.presentation.notes.NotesViewModel
import com.bm.docathome.noteapp.feature_note.presentation.util.Screen
import com.bm.docathome.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteAppTheme {
                val notesViewModel = hiltViewModel<NotesViewModel>()
                val addEditNoteViewModel = hiltViewModel<AddEditNoteViewModel>()
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.NotesScreen.route
                    ) {
                        composable(route = Screen.NotesScreen.route) {
                            NotesScreen(navController = navController, notesViewModel)
                        }
                        composable(
                            route = Screen.AddEditNoteScreen.route +
                                    "?noteId={noteId}&noteColor={noteColor}",
                            arguments = listOf(
                                navArgument(
                                    name = "noteId"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                                navArgument(
                                    name = "noteColor"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                            )
                        ) {
                            val color = it.arguments?.getInt("noteColor") ?: -1
                            AddEditNoteScreen(
                                navController = navController,
                                viewModel = addEditNoteViewModel,
                                noteColor = color
                            )
                        }
                    }
                }
            }
        }

    }
}