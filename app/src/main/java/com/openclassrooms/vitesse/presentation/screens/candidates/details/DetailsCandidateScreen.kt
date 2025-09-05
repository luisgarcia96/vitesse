package com.openclassrooms.vitesse.presentation.screens.candidates.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.presentation.screens.candidates.edit.EditCandidateViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsCandidateScreen(
  dao: CandidateDao,
  candidateId: Int,
  onBack: () -> Unit,
  onEdit: (Int) -> Unit
) {
  val viewModel = remember(candidateId) { EditCandidateViewModel(dao, candidateId) }
  val state by viewModel.state.collectAsState()
  var showConfirm by remember { mutableStateOf(false) }

  val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("${state.firstName} ${state.lastName}".trim(), maxLines = 1) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        actions = {
          IconButton(onClick = { viewModel.setFavorite(!state.isFavorite) }) {
            val icon = if (state.isFavorite) Icons.Filled.Star else Icons.Outlined.Star
            Icon(icon, contentDescription = null)
          }
          IconButton(onClick = { onEdit(state.id) }) {
            Icon(Icons.Filled.Edit, contentDescription = null)
          }
          IconButton(onClick = { showConfirm = true }) {
            Icon(Icons.Filled.Delete, contentDescription = null)
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.first_name), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = state.firstName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
          Text(text = stringResource(R.string.last_name), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = state.lastName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
        }
      }

      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.phone_number), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = state.phoneNumber)
          Text(text = stringResource(R.string.email), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = state.email)
        }
      }

      Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = stringResource(R.string.birth_date), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = state.birthDate?.format(dateFormatter) ?: "")
          Text(text = stringResource(R.string.expected_salary), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Text(text = state.expectedSalary)
        }
      }

      if (state.notes.isNotBlank()) {
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.notes), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = state.notes)
          }
        }
      }
    }
  }

  if (showConfirm) {
    AlertDialog(
      onDismissRequest = { showConfirm = false },
      title = { Text(stringResource(R.string.confirm_delete_title)) },
      text = { Text(stringResource(R.string.confirm_delete_message)) },
      confirmButton = {
        TextButton(onClick = {
          showConfirm = false
          viewModel.deleteCandidate()
          onBack()
        }) { Text(stringResource(R.string.confirm)) }
      },
      dismissButton = {
        TextButton(onClick = { showConfirm = false }) { Text(stringResource(R.string.cancel)) }
      }
    )
  }
}

