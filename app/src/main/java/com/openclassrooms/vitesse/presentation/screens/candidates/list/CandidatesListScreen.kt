package com.openclassrooms.vitesse.presentation.screens.candidates.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatesListScreen(
  onAddCandidate: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text("Liste des candidats") })
    },
    floatingActionButton = {
      Button(onClick = onAddCandidate) {
        Text("Ajouter")
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.Top
    ) {
      // TODO: Replace with your LazyColumn of candidates
      Text("Ici apparaît la liste des candidats")
    }
  }
}