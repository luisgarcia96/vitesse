package com.openclassrooms.vitesse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import com.openclassrooms.vitesse.data.database.CandidateDatabase
import com.openclassrooms.vitesse.presentation.navigation.AppNavGraph
import com.openclassrooms.vitesse.ui.theme.VitesseTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // build Room and grab DAO
    val db = Room.databaseBuilder(
      applicationContext,
      CandidateDatabase::class.java,
      "candidate_db"
    )
      .fallbackToDestructiveMigration()
      .build()
    val dao = db.dao

    setContent {
      VitesseTheme {
        AppNavGraph(candidateDao = dao)
      }
    }
  }
}