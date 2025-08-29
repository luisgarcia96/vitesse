package com.openclassrooms.vitesse.presentation.screens.candidates.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.vitesse.data.dao.CandidateDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatesListScreen(
  dao: CandidateDao,
  onAddCandidate: () -> Unit
) {
  // collect the Flow from your DAO
  val candidates by dao.getAllCandidates().collectAsState(initial = emptyList())

  val displayNames = candidates.map { "${it.firstName} ${it.lastName}" }
  CandidatesListContent(
    displayNames = displayNames,
    onAddCandidate = onAddCandidate
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidatesListContent(
  displayNames: List<String>,
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
  ) { paddingValues ->
    if (displayNames.isEmpty()) {
      Box(
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text("Aucun candidat")
      }
    } else {
      LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
      ) {
        items(displayNames) { name ->
          Text(name)
        }
      }
    }
  }
}

@Preview(showBackground = true, name = "Empty state")
@Composable
private fun CandidatesList_Empty_Preview() {
  CandidatesListContent(
    displayNames = emptyList(),
    onAddCandidate = {}
  )
}

@Preview(showBackground = true, name = "With items")
@Composable
private fun CandidatesList_WithItems_Preview() {
  CandidatesListContent(
    displayNames = listOf("Ada Lovelace", "Alan Turing", "Grace Hopper"),
    onAddCandidate = {}
  )
}