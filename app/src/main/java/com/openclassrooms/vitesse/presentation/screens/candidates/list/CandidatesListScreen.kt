package com.openclassrooms.vitesse.presentation.screens.candidates.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.data.dao.CandidateDao

private data class CandidateUi(
  val firstName: String,
  val lastName: String,
  val notes: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatesListScreen(
  dao: CandidateDao,
  onAddCandidate: () -> Unit
) {
  // collect the Flow from your DAO
  val candidates by dao.getAllCandidates().collectAsState(initial = emptyList())

  val uiCandidates = candidates.map { CandidateUi(
    firstName = it.firstName,
    lastName = it.lastName,
    notes = it.notes
  ) }
  CandidatesListContent(
    candidates = uiCandidates,
    onAddCandidate = onAddCandidate
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidatesListContent(
  candidates: List<CandidateUi>,
  onAddCandidate: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text(stringResource(R.string.search_candidate))})
    },
    floatingActionButton = {
      Button(onClick = onAddCandidate) {
        Text(stringResource(R.string.add))
      }
    }
  ) { paddingValues ->
    if (candidates.isEmpty()) {
      Box(
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(stringResource(R.string.no_candidate))
      }
    } else {
      LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
      ) {
        items(candidates) { candidate ->
          CandidateItem(candidate = candidate)
        }
      }
    }
  }
}

@Composable
private fun CandidateItem(
  candidate: CandidateUi,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Simple circular avatar with initials
    val initials = "${candidate.firstName.firstOrNull()?.uppercaseChar() ?: '?'}${candidate.lastName.firstOrNull()?.uppercaseChar() ?: ""}"
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = initials,
        style = MaterialTheme.typography.titleMedium
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column {
      Text(
        text = "${candidate.firstName} ${candidate.lastName}".uppercase(),
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
      )
      if (!candidate.notes.isNullOrBlank()) {
        Text(
          text = candidate.notes,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }
}

@Preview(showBackground = true, name = "Empty state")
@Composable
private fun CandidatesList_Empty_Preview() {
  CandidatesListContent(
    candidates = emptyList(),
    onAddCandidate = {}
  )
}

@Preview(showBackground = true, name = "With items")
@Composable
private fun CandidatesList_WithItems_Preview() {
  CandidatesListContent(
    candidates = listOf(
      CandidateUi("Ada", "Lovelace", "Math pioneer"),
      CandidateUi("Alan", "Turing", "Father of AI"),
      CandidateUi("Grace", "Hopper", "COBOL inventor")
    ),
    onAddCandidate = {}
  )
}