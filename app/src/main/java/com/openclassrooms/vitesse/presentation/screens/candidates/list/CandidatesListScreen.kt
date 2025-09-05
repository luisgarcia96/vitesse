package com.openclassrooms.vitesse.presentation.screens.candidates.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.data.dao.CandidateDao
 

private data class CandidateUi(
  val id: Int,
  val firstName: String,
  val lastName: String,
  val notes: String?,
  val isFavorite: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatesListScreen(
  dao: CandidateDao,
  onAddCandidate: () -> Unit,
  onOpenCandidate: (Int) -> Unit
) {
  // collect the Flow from your DAO
  val candidates by dao.getAllCandidates().collectAsState(initial = emptyList())
  val favorites by dao.getFavoriteCandidates().collectAsState(initial = emptyList())

  val uiCandidates = candidates.map { CandidateUi(
    id = it.id,
    firstName = it.firstName,
    lastName = it.lastName,
    notes = it.notes,
    isFavorite = it.isFavorite
  ) }
  val uiFavorites = favorites.map { CandidateUi(
    id = it.id,
    firstName = it.firstName,
    lastName = it.lastName,
    notes = it.notes,
    isFavorite = it.isFavorite
  ) }
  CandidatesListContent(
    candidates = uiCandidates,
    favorites = uiFavorites,
    onAddCandidate = onAddCandidate,
    onOpenCandidate = onOpenCandidate
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidatesListContent(
  candidates: List<CandidateUi>,
  favorites: List<CandidateUi>,
  onAddCandidate: () -> Unit,
  onOpenCandidate: (Int) -> Unit
) {
  var query by rememberSaveable { mutableStateOf("") }
  var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
  Scaffold(
    topBar = {
      Column {
        CenterAlignedTopAppBar(
          modifier = Modifier
            .padding(top = 16.dp),
          title = {
            Box(
              modifier = Modifier
                .fillMaxWidth(),
              contentAlignment = Alignment.Center
            ) {
              OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(stringResource(R.string.search_candidate)) },
                singleLine = true,
                shape = CircleShape,
                trailingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(56.dp)
              )
            }
          }
        )
        TabRow(
          selectedTabIndex = selectedTabIndex,
          modifier = Modifier.fillMaxWidth()
        ) {
          Tab(
            selected = selectedTabIndex == 0,
            onClick = { selectedTabIndex = 0 },
            text = { Text(stringResource(R.string.all)) }
          )
          Tab(
            selected = selectedTabIndex == 1,
            onClick = { selectedTabIndex = 1 },
            text = { Text(stringResource(R.string.favorites)) }
          )
        }
      }
    },
    floatingActionButton = {
      Button(onClick = onAddCandidate) {
        Text(stringResource(R.string.add))
      }
    }
  ) { paddingValues ->
    val source = if (selectedTabIndex == 0) candidates else favorites
    val filtered = if (query.isBlank()) source else source.filter { c ->
      "${c.firstName} ${c.lastName}".contains(query, ignoreCase = true) ||
      (c.notes?.contains(query, ignoreCase = true) == true)
    }

    if (filtered.isEmpty()) {
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
        items(filtered) { candidate ->
          CandidateItem(
            candidate = candidate,
            onClick = { onOpenCandidate(candidate.id) }
          )
        }
      }
    }
  }
}

@Composable
private fun CandidateItem(
  candidate: CandidateUi,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {}
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
      .clickable { onClick() },
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
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 2,
          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
    favorites = emptyList(),
    onAddCandidate = {},
    onOpenCandidate = {}
  )
}

@Preview(showBackground = true, name = "With items")
@Composable
private fun CandidatesList_WithItems_Preview() {
  CandidatesListContent(
    candidates = listOf(
      CandidateUi(1, "Ada", "Lovelace", "Math pioneer", true),
      CandidateUi(2, "Alan", "Turing", "Father of AI", false),
      CandidateUi(3, "Grace", "Hopper", "Long description here so it can be truncated at the second line blablabla boiznzefu kjherz zoz zn'", false)
    ),
    favorites = listOf(
      CandidateUi(1, "Ada", "Lovelace", "Math pioneer", true)
    ),
    onAddCandidate = {},
    onOpenCandidate = {}
  )
}
